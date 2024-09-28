package chapter9

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment


object LogTest1 {
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
    //打印输出schema
    table.printSchema()
    //创建临时表
    tableEnv.createTemporaryView("access_log", table)
    //计算每日PV
    val table2 = tableEnv.sqlQuery("select createDate, " +
      "count(*) as logCount " +
      "from access_log " +
      "group by createDate ")
    //打印输出流
    tableEnv.toChangelogStream(table2).print()
    //开始执行
    env.execute()
  }
}
