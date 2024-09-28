package chapter5

import org.apache.flink.api.common.functions.JoinFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

object JoinTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    //数据流1
    val stream1 = env.fromElements(
      ("sensor_1", 1000L, 1.0),
      ("sensor_2", 1000L, 2.0),
      ("sensor_1", 2000L, 3.0),
      ("sensor_2", 6000L, 4.0)
    ).assignAscendingTimestamps(_._2)
    //数据流2
    val stream2 = env.fromElements(
      ("sensor_1", 3000L, 5.0),
      ("sensor_2", 3000L, 6.0),
      ("sensor_1", 4000L, 7.0),
      ("sensor_2", 4000L, 8.0)
    ).assignAscendingTimestamps(_._2)
    // 窗口操作
    stream1.join(stream2)
      .where(_._1)
      .equalTo(_._1)
      .window(TumblingEventTimeWindows.of(Time.seconds(5)))
      .apply(new JoinFunction[(String, Long, Double), (String, Long, Double), String] {
        override def join(in1: (String, Long, Double), in2: (String, Long, Double)): String = {
          in1 + "->" + in2
        }
      }).print()
    //开始执行
    env.execute()
  }
}
