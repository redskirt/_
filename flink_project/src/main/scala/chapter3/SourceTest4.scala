package chapter3

import org.apache.flink.streaming.api.scala._
/**
 *
 * 自定义数据源
 */
object SourceTest4 {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度1
    env.setParallelism(1)
    //添加数据源
    val dataStream = env.addSource(new SensorSource(10))
    // 打印输出
    dataStream.print()
    env.execute()
  }
}

