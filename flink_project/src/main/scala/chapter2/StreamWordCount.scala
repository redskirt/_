package chapter2

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
/**
 * 流处理单词统计
 */
object StreamWordCount {

  def main(args: Array[String]): Unit = {

    //发送数据的主机名
    val host = "hadoop1"
    //发送数据的端口号
    val port = 5555
    //获取流处理的执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度为：1
    env.setParallelism(1)
    //从Socket中读取一行
    val textDataSteam = env.socketTextStream(host, port)
    //读取数据，分词后进行统计
    val wordCountDataStream = textDataSteam
      //按照空格进行分词
      .flatMap(_.split(" "))
      //构造元组，（单词，1）
      .map((_, 1))
      //按照第一个字段进行分组聚合
      .keyBy(0)
      //按照第二个字段进行汇总
      .sum(1)

    wordCountDataStream.print()
    //执行
    env.execute()
  }
}
