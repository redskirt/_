package chapter10

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

object OrderTest1 {
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
    //打印Schema
    table.printSchema()

    //按照日期，统计订单金额的最大值，最小值和均值,总金额
    val table2 = tableEnv.sqlQuery("select createDate, " +
      "min(orderPrice) as minOrderPrice, " +
      "max(orderPrice) as maxOrderPrice, " +
      "avg(orderPrice) as avgOrderPrice, " +
      "sum(orderPrice) as sumOrderPrice, " +
      "count(*) as countOrder " +
      "from order_info " +
      "group by createDate ")
    //打印schema
    table2.printSchema()

    // 定义SQL
    val orderDateSql: String =
      """
        |create table order_date (
        | createDate varchar(10) not null,
        | minOrderPrice float not null,
        | maxOrderPrice float not null,
        | avgOrderPrice float not null,
        | sumOrderPrice float not null,
        | countOrder bigint not null,
        | primary key (createDate) not enforced
        | ) with (
        |   'connector.type' = 'jdbc',
        |   'connector.url' = 'jdbc:mysql://localhost:3306/flink_project?useSSL=false',
        |   'connector.table' = 'order_date',
        |   'connector.driver' = 'com.mysql.jdbc.Driver',
        |   'connector.username' = 'root',
        |   'connector.password' = 'root123456'
        | )
        |""".stripMargin
    //执行SQL建表
    tableEnv.executeSql(orderDateSql)
    //执行插入操作
    table2.executeInsert("order_date")

    //按照小时，统计订单金额的最大值，最小值和均值
    val table3 = tableEnv.sqlQuery("select createHour, " +
      "min(orderPrice) as minOrderPrice, " +
      "max(orderPrice) as maxOrderPrice, " +
      "avg(orderPrice) as avgOrderPrice, " +
      "sum(orderPrice) as sumOrderPrice, " +
      "count(*) as countOrder " +
      "from order_info " +
      "group by createHour ")
    //打印schema
    table3.printSchema()

    // 定义SQL
    val orderHourSql: String =
      """
        |create table order_hour (
        | createHour int not null,
        | minOrderPrice float not null,
        | maxOrderPrice float not null,
        | avgOrderPrice float not null,
        | sumOrderPrice float not null,
        | countOrder bigint not null,
        | primary key (createHour) not enforced
        | ) with (
        |   'connector.type' = 'jdbc',
        |   'connector.url' = 'jdbc:mysql://localhost:3306/flink_project?useSSL=false',
        |   'connector.table' = 'order_hour',
        |   'connector.driver' = 'com.mysql.jdbc.Driver',
        |   'connector.username' = 'root',
        |   'connector.password' = 'root123456'
        | )
        |""".stripMargin
    //执行SQL建表
    tableEnv.executeSql(orderHourSql)
    //执行插入操作
    table3.executeInsert("order_hour")


    //按照地址，统计订单金额的最大值，最小值和均值
    val table4 = tableEnv.sqlQuery("select addr, " +
      "min(orderPrice) as minOrderPrice, " +
      "max(orderPrice) as maxOrderPrice, " +
      "avg(orderPrice) as avgOrderPrice, " +
      "sum(orderPrice) as sumOrderPrice, " +
      "count(*) as countOrder " +
      "from order_info " +
      "group by addr ")
    //打印schema
    table4.printSchema()

    // 定义SQL
    val orderAddrSql: String =
      """
        |create table order_addr (
        | addr varchar(10) not null,
        | minOrderPrice float not null,
        | maxOrderPrice float not null,
        | avgOrderPrice float not null,
        | sumOrderPrice float not null,
        | countOrder bigint not null,
        | primary key (addr) not enforced
        | ) with (
        |   'connector.type' = 'jdbc',
        |   'connector.url' = 'jdbc:mysql://localhost:3306/flink_project?useSSL=false',
        |   'connector.table' = 'order_addr',
        |   'connector.driver' = 'com.mysql.jdbc.Driver',
        |   'connector.username' = 'root',
        |   'connector.password' = 'root123456'
        | )
        |""".stripMargin
    //执行SQL建表
    tableEnv.executeSql(orderAddrSql)
    //执行插入操作
    table4.executeInsert("order_addr")

    //开始执行
    env.execute()
  }
}
