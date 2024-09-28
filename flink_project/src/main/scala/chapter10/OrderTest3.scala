package chapter10

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.util.Collector

object OrderTest3 {
  def main(args: Array[String]): Unit = {
    //创建运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //从文件中读取数据
    val stream: DataStream[String] = env.readTextFile("data/order.csv")
    //转换格式
    val stream2: DataStream[OrderInfo] = stream.map(strOrderInfo => {
      OrderUtils.read(strOrderInfo)
    })

    //分流
    val stream3: DataStream[OrderInfo] = stream2
      .process(new SplitOrderProcessor(1000.0F))

    //警告流
    val warnStream = stream3.getSideOutput(new OutputTag[OrderInfo]("warn"))
    // 创建表环境
    val tableEnv = StreamTableEnvironment.create(env)
    // 将DataStream转换成表
    val table = tableEnv.fromDataStream(warnStream)

    //
    // 写入MySQL数据库中
    val orderWarnSql: String =
    """
      |create table order_warn (
      | createTime varchar(20) not null,
      | createDate varchar(10) not null,
      | createHour int not null,
      | payTime varchar(20) not null,
      | orderPrice float not null,
      | payPrice float not null,
      | returnPrice float not null,
      | addr varchar(10) not null
      | ) with (
      |   'connector.type' = 'jdbc',
      |   'connector.url' = 'jdbc:mysql://localhost:3306/flink_project?useSSL=false',
      |   'connector.table' = 'order_warn',
      |   'connector.driver' = 'com.mysql.jdbc.Driver',
      |   'connector.username' = 'root',
      |   'connector.password' = 'root123456'
      | )
      |""".stripMargin
    //执行SQL建表
    tableEnv.executeSql(orderWarnSql)
    //执行插入操作
    table.executeInsert("order_warn")


    //开始执行
    env.execute()
  }


  class SplitOrderProcessor(maxOrderPrice: Float) extends ProcessFunction[OrderInfo, OrderInfo] {

    override def processElement(orderInfo: OrderInfo, ctx: ProcessFunction[OrderInfo, OrderInfo]#Context, out: Collector[OrderInfo]): Unit = {
      // 大额订单退款
      if (orderInfo.returnPrice>maxOrderPrice) {
        ctx.output(new OutputTag[OrderInfo]("warn"), orderInfo)
      }
     else {
        //正常流
        ctx.output(new OutputTag[OrderInfo]("normal"), orderInfo)
      }
    }
  }
}
