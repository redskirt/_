package chapter3

import org.apache.flink.streaming.api.scala._

/**
 * 从Socket中读取数据
 */
object SourceTest3 {
  def main(args: Array[String]): Unit = {
    // 获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //从文件中读取数据
    val dataStream: DataStream[String] = env.socketTextStream("hadoop1",5555)
    //输出数据流
    dataStream.print()
    //开始执行
    env.execute()
  }
}
