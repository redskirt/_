package chapter9

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment


object LogTest2 {
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

    //状态码是500的数据
    val table2 = tableEnv.sqlQuery("select * " +
      "from access_log " +
      "where status=500 ")
    //打印输出流
    tableEnv.toChangelogStream(table2).print("status<>200")
    //响应时间过长
    val table3 = tableEnv.sqlQuery("select * " +
      "from access_log " +
      "where responseTime>1 ")
    //打印输出流
    tableEnv.toChangelogStream(table3).print("responseTime>1")
    //开始执行
    env.execute()
  }


}
