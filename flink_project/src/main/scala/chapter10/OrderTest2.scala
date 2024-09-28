package chapter10

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

object OrderTest2 {
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
    // 创建表环境
    val tableEnv = StreamTableEnvironment.create(env)
    // 将DataStream转换成表
    val table = tableEnv.fromDataStream(stream2)
    //创建临时表
    tableEnv.createTemporaryView("order_info", table)

    //执行SQL
    val table2 = tableEnv.sqlQuery("select * " +
      "from order_info " +
      "where payPrice<=0 ")

    // 定义SQL
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
    table2.executeInsert("order_warn")

    //执行SQL
    val table3 = tableEnv.sqlQuery("select createDate, " +
      "sum(orderPrice) as sumOrderPrice, " +
      "count(*) as countOrder " +
      "from order_info " +
      "where payPrice<=0 " +
      "group by createDate")
    //输出Schema
    table3.printSchema()

    // 定义SQL
    val orderNopaySql: String =
      """
        |create table order_nopay (
        | createDate varchar(10) not null,
        | sumOrderPrice float not null,
        | countOrder bigint not null,
        | primary key (createDate) not enforced
        | ) with (
        |   'connector.type' = 'jdbc',
        |   'connector.url' = 'jdbc:mysql://localhost:3306/flink_project?useSSL=false',
        |   'connector.table' = 'order_nopay',
        |   'connector.driver' = 'com.mysql.jdbc.Driver',
        |   'connector.username' = 'root',
        |   'connector.password' = 'root123456'
        | )
        |""".stripMargin
    //执行SQL建表
    tableEnv.executeSql(orderNopaySql)
    //执行插入操作
    table3.executeInsert("order_nopay")

    //开始执行
    env.execute()
  }

}
