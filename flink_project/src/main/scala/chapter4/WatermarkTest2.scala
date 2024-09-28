package chapter4

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.scala._

/**
 * 自定义的水位线的应用
 */
object WatermarkTest2 {

  def main(args: Array[String]) {

    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //时间语义： 事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    //数据源
    val dataStream = env.fromElements(
      ("sensor_1", 1673711312000L, 10.0),
      ("sensor_1", 1673711311000L, 20.0),
      ("sensor_1", 1673711329000L, 30.0),
      ("sensor_1", 1673711322000L, 40.0),
      ("sensor_2", 1673711328000L, 20.0),
      ("sensor_3", 1673711319000L, 30.0),
      ("sensor_2", 1673711322000L, 40.0)
    )
    //分配时间戳和水位线
    dataStream
      .assignTimestampsAndWatermarks(new PunctuatedAssigner)
    //开始执行
    env.execute()
  }

  /**
   * 自定义水位线
   */
  class PunctuatedAssigner extends AssignerWithPunctuatedWatermarks[(String, Long, Double)] {

    // 分钟转换为毫秒
    val bound: Long = 10 * 1000

    //检查并获取下一个水位线
    override def checkAndGetNextWatermark(r: (String, Long, Double), extractedTS: Long): Watermark = {
      if (r._1 == "sensor_1") {
        // 只向sensor_1发出水位线
        println("send to sensor"+r+" watermark timestamp:"+(extractedTS - bound))
        new Watermark(extractedTS - bound)
      } else {
        // 其他不发出
        null
      }
    }
    //提取时间戳
    override def extractTimestamp(r: (String, Long, Double), previousTS: Long): Long = {
      // 提取时间戳
      r._2
    }
  }
}


