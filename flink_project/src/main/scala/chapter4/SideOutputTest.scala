package chapter4

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

object SideOutputTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    //数据源
    val dataStream = env.fromElements(
      ("sensor_1", 1673711312000L, 10.0),
      ("sensor_1", 1673711311000L, 20.0),
      ("sensor_1", 1673711329000L, 30.0),
      ("sensor_1", 1673711322000L, 99.0),
      ("sensor_2", 1673711328000L, 20.0),
      ("sensor_3", 1673711319000L, 97.0),
      ("sensor_2", 1673711322000L, 40.0)
    )
    // 用 ProcessFunction的侧输出流实现分流操作
    val normalTempStream: DataStream[(String,Long,Double)] = dataStream
      .process( new SplitTempProcessor(90.0) )
    //获取侧输出流，ID为high-temp流
    val highTempStream = normalTempStream.getSideOutput( new OutputTag[(String,Long,Double)]("high-temp") )
    // 输出高温流
    highTempStream.print("high")
    //输出正常流
    normalTempStream.print("normal")
    //开始执行
    env.execute()
  }

  /**
   * 自定义ProcessFunction 用于区分高低温度的数据
   * @param threshold 温度的阈值
   */
  class SplitTempProcessor(threshold: Double) extends ProcessFunction[(String,Long,Double), (String,Long,Double)]{
    override def processElement(value: (String,Long,Double), ctx: ProcessFunction[(String,Long,Double), (String,Long,Double)]#Context, out: Collector[(String,Long,Double)]): Unit = {
      // 判断当前数据的温度值，如果不超过阈值，输出到主流；如果大于阈值，输出到侧输出流
      if( value._3 <= threshold ){
        out.collect(value)
      } else {
        //侧输出流
        ctx.output(new OutputTag[(String,  Long,Double)]("high-temp"), (value._1, value._2, value._3) )
      }
    }
  }
}

