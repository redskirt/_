package chapter6

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala._


object WordCount1 {
  def main(args: Array[String]): Unit = {
    //1.创建表执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val settings = EnvironmentSettings
      .newInstance()
      .useBlinkPlanner()
      .inStreamingMode()
      .build()
    val tableEnv = StreamTableEnvironment.create(env, settings)

    //2.读取文件作为输入流，进行简单数据类型处理
    val inputStream: DataStream[String] = env.readTextFile(
      "data/wordcount.txt"
    )
    //将单词转为(单词,1)的形式
    val dataStream: DataStream[(String, Int)] = inputStream
      .flatMap(_.split(" ")) //按照空格分割单词
      .map((_, 1))

    //3.数据流DataStream转化成Table表，并指定相应字段
    val inputTable: Table = tableEnv.fromDataStream[(String, Int)](
      dataStream,
      $"word",
      $"count"
    )
    //4.使用Table API对Table表进行关系操作
    val resultTable: Table = inputTable
      .groupBy($"word")
      .select($"word", $"count".sum)

    //5.结果Table转成DataStream数据流，并输出到控制台
    //resultTable.toRetractStream[(String, Long)].print()
    tableEnv.toChangelogStream(resultTable).print()
    //6.任务执行
    env.execute()
  }
}
