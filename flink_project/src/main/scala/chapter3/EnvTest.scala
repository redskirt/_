package chapter3

import org.apache.flink.streaming.api.scala._

/**
 * 获取执行环境
 */
object EnvTest {
  def main(args: Array[String]): Unit = {
    // 获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //开始执行
    env.execute()
  }
}
