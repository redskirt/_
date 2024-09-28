package chapter5

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

/**
 * 合流
 */

object UnionStream {

  def main(args: Array[String]): Unit = {

    //创建运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    //数据源1
    val dataStream1 = env.fromElements(
      ("sensor_1", 1673711312000L, 9.0),
      ("sensor_1", 1673711311000L, 10.0),
      ("sensor_1", 1673711329000L, 30.0),
      ("sensor_1", 1673711322000L, 99.0)
    )
    //数据源2
    val dataStream2 = env.fromElements(
      ("sensor_2", 1673711328000L, 20.0),
      ("sensor_2", 1673711319000L, 97.0),
      ("sensor_2", 1673711322000L, 40.0)
    )
    //合流
    val tempStream: DataStream[(String, Long, Double)] = dataStream1.union(dataStream2)
    //输出
    tempStream.print()
    //开始执行
    env.execute()
  }
}

