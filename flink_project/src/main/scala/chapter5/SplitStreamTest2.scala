package chapter5

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

/**
 * 分流
 */
object SplitStreamTest2 {
  def main(args: Array[String]): Unit = {

    //创建运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    //数据源
    val dataStream = env.fromElements(
      ("sensor_1", 1673711312000L, 9.0),
      ("sensor_1", 1673711311000L, 10.0),
      ("sensor_1", 1673711329000L, 30.0),
      ("sensor_1", 1673711322000L, 99.0),
      ("sensor_2", 1673711328000L, 20.0),
      ("sensor_3", 1673711319000L, 97.0),
      ("sensor_2", 1673711322000L, 40.0)
    )
    //按照高温阈值和低温阈值进行分流
    val tempStream: DataStream[(String, Long, Double)] = dataStream
      .process(new SplitTempProcessor(10.0, 90.0))
    //低温流
    val lowTempStream = tempStream.getSideOutput(new OutputTag[(String, Long, Double)]("low-temp"))
    //正常流
    val normalTempStream = tempStream.getSideOutput(new OutputTag[(String, Long, Double)]("normal-temp"))
    //高温流
    val highTempStream = tempStream.getSideOutput(new OutputTag[(String, Long, Double)]("high-temp"))
    //输出低温流
    lowTempStream.print("low-temp")
    //输出正常流
    normalTempStream.print("normal-temp")
    //输出高温流
    highTempStream.print("high-temp")
    //开始执行
    env.execute()
  }

  /**
   * 自定义ProcessFunction 用于区分高低温度的数据
   * @param minTemp 低温阈值
   * @param maxTemp 高温阈值
   */
  class SplitTempProcessor(minTemp: Double, maxTemp: Double) extends ProcessFunction[(String, Long, Double), (String, Long, Double)] {
    override def processElement(value: (String, Long, Double), ctx: ProcessFunction[(String, Long, Double), (String, Long, Double)]#Context, out: Collector[(String, Long, Double)]): Unit = {
      // 低温流
      if (value._3 < minTemp) {
        ctx.output(new OutputTag[(String, Long, Double)]("low-temp"), (value._1, value._2, value._3))
      }
      //高温流
      else if (value._3 > maxTemp) {
        ctx.output(new OutputTag[(String, Long, Double)]("high-temp"), (value._1, value._2, value._3))
      } else {
        //正常流
        ctx.output(new OutputTag[(String, Long, Double)]("normal-temp"), (value._1, value._2, value._3))
      }
    }
  }
}

