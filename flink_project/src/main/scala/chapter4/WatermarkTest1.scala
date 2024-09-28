package chapter4

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.scala._

/**
 * 自定义的水位线
 *
 */
object WatermarkTest1 {

  def main(args: Array[String]) {
    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    //生成watermark的时间间隔
    env.getConfig.setAutoWatermarkInterval(10 * 1000)
    //时间语义： 事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    // 数据源
    val dataStream = env.fromElements(
      ("sensor_1", 1673711312000L, 10.0),
      ("sensor_1", 1673711311000L, 20.0),
      ("sensor_1", 1673711309000L, 30.0),
      ("sensor_1", 1673711328000L, 40.0),
      ("sensor_1", 1673711329000L, 30.0),
      ("sensor_1", 1673711322000L, 40.0)
    )

    //分配时间戳和水位线
    dataStream
      .assignTimestampsAndWatermarks(new PeriodicAssigner)
    //开始执行
    env.execute()
  }

  /**
   * 周期性生成水位线
   */
  class PeriodicAssigner extends AssignerWithPeriodicWatermarks[(String, Long, Double)] {
    // 秒
    val bound: Long = 10 * 1000
    // 最大时间戳，默认最小值
    var maxTs: Long = Long.MinValue

    /**
     * 水位线  当前时间戳-bound
     *
     * @return
     */
    override def getCurrentWatermark: Watermark = {
      println("maxTs:" + maxTs + "  watermark:" + (maxTs - bound))
      new Watermark(maxTs - bound)
    }

    /**
     * 提取时间戳
     *
     * @param sensor 传感器数据的三元组
     * @param previousTimestamp
     * @return
     */
    override def extractTimestamp(sensor: (String, Long, Double), previousTimestamp: Long): Long = {
      val beforeUpdateMaxTs = maxTs
      // 更新最大时间戳
      maxTs = maxTs.max(sensor._2)
      println("sensorTimestamp:" + sensor._2 + "  maxTimestamp:" + beforeUpdateMaxTs + " updatedTimestamp:" + maxTs)
      // 返回当前时间戳
      sensor._2
    }
  }
}
