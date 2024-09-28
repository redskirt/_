package chapter4

import chapter3.SensorSource
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time


object TimeAndWatermarkTest {
  def main(args: Array[String]): Unit = {
    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置水位线时间间隔
    env.getConfig.setAutoWatermarkInterval(1 * 1000)
    //设置并行度
    env.setParallelism(1)
    //设置时间语义 事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    // 随机数据源
    val dataStream = env.addSource(new SensorSource(1))
      // watermark延迟5秒，
      .assignTimestampsAndWatermarks(
        //按照固定的延迟发出watermark
        new BoundedOutOfOrdernessTimestampExtractor[(String,Long,Double)](Time.seconds(2)) {
          // 提取时间戳
          override def extractTimestamp(element: (String,Long,Double)): Long = element._2
        })

    val resultStream: DataStream[(String,Long,Double)] = dataStream.keyBy(0)
      // 时间滚动
      .timeWindow(Time.seconds(5), Time.seconds(5))
      //允许处理迟到数据
      //对于watermark超过end-of-window之后，还允许有一段时间（以事件时间来衡量）来等待之前的数据
     .allowedLateness(Time.seconds(1))
      //迟到数据进入侧输出流
      .sideOutputLateData(new OutputTag[(String,Long,Double)]("late"))
      .reduce((sensor, newSensor) => {
        (sensor._1, sensor._2, sensor._3.max(newSensor._3))
      })
    //侧输出流数据
    val lateStream = resultStream.getSideOutput(new OutputTag[(String,Long,Double)]("late"))
    // 输出侧输出流
    lateStream.print("late")
    //输出结果
    resultStream.print("result")
   //开始执行
    env.execute()
  }
}

