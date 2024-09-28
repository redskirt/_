package chapter9

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment


object LogTest4 {
  def main(args: Array[String]): Unit = {
    //创建运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //从文件中读取数据
    val stream: DataStream[String] = env.readTextFile("data/access.log")
    //转换为AccessLog对象
    val stream2: DataStream[AccessLog] = stream.map(json => {
      AccessLogUtils.read(json)
    })
    // 创建表环境
    val tableEnv = StreamTableEnvironment.create(env)
    // 将DataStream转换成表
    val table = tableEnv.fromDataStream(stream2)
    //创建临时表
    tableEnv.createTemporaryView("access_log", table)
    //每日响应时间分析
    val table2 = tableEnv.sqlQuery("select createDate, " +
      "min(responseTime) as minResponseTime, " +
      "max(responseTime) as maxResponseTime, " +
      "avg(responseTime) as avgResponseTime " +
      "from access_log " +
      "where status=200 " +
      "group by createDate ")
    // 写入MySQL数据库中
    val logTimeSql: String =
      """
        |create table log_time (
        | createDate varchar(10) not null,
        | minResponseTime float not null,
        | maxResponseTime float not null,
        | avgResponseTime float not null,
        | primary key (createDate) not enforced
        | ) with (
        |   'connector.type' = 'jdbc',
        |   'connector.url' = 'jdbc:mysql://localhost:3306/flink_project?useSSL=false',
        |   'connector.table' = 'log_time',
        |   'connector.driver' = 'com.mysql.jdbc.Driver',
        |   'connector.username' = 'root',
        |   'connector.password' = 'root123456'
        | )
        |""".stripMargin
    //执行SQL建表
    tableEnv.executeSql(logTimeSql)
    //执行插入操作
    table2.executeInsert("log_time")
    // 转换成流打印输出
    tableEnv.toChangelogStream(table2)
      .print()
    //每小时的流量分析
    val table3 = tableEnv.sqlQuery("select createHour," +
      "sum(size) as totalSize, " +
      "avg(size) as avgSize " +
      "from access_log " +
      "where status=200 " +
      "group by createHour")
    // 写入MySQL数据库中
    val logSizeSql: String =
      """
        |create table log_size (
        | createHour int not null,
        | totalSize bigint not null,
        | avgSize float not null,
        | primary key (createHour) not enforced
        | ) with (
        |   'connector.type' = 'jdbc',
        |   'connector.url' = 'jdbc:mysql://localhost:3306/flink_project?useSSL=false',
        |   'connector.table' = 'log_size',
        |   'connector.driver' = 'com.mysql.jdbc.Driver',
        |   'connector.username' = 'root',
        |   'connector.password' = 'root123456'
        | )
        |""".stripMargin
    //执行SQL建表
    tableEnv.executeSql(logSizeSql)
    //执行插入操作
    table3.executeInsert("log_size")
    // 转换成流打印输出
    tableEnv.toChangelogStream(table3)
      .print()
    //开始执行
    env.execute()
  }


}
