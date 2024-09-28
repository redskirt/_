package chapter5

import chapter3.SensorSource
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, KeyedStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector

import java.time.Duration

object KeyedStateTest {

  def main(args: Array[String]) {
    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置检查点时间间隔
    env.getCheckpointConfig.setCheckpointInterval(10 * 1000)
    // 配置水位线的间隔
    env.getConfig.setAutoWatermarkInterval(1000L)
    // 数据源
    val dataStream: DataStream[(String, Long, Double)] = env
      .addSource(new SensorSource(10))
      // 分配时间和水位线
      .assignTimestampsAndWatermarks(
        //指定水印生成策略:周期性策略
        WatermarkStrategy.forBoundedOutOfOrderness[(String, Long, Double)](
          Duration.ofSeconds(3))
          .withTimestampAssigner(new SerializableTimestampAssigner[(String, Long, Double)] {
            //指定事件时间戳
            override def extractTimestamp(element: (String, Long, Double), recordTimestamp: Long): Long = element._2
          }))
    //按照传感器ID进行分组
    val keyedSensorData: KeyedStream[(String, Long, Double), String] = dataStream.keyBy(_._1)
    //设定报警
    val dataStream2: DataStream[(String, Double, Double)] = keyedSensorData
      .flatMap(new TemperatureAlertFunction(1.7))
    // 控制台输出
    dataStream2.print()
    //开始执行
    env.execute()
  }

  /**
   * 设定报警
   *
   * @param threshold 阈值
   */
  class TemperatureAlertFunction(val threshold: Double)
    extends RichFlatMapFunction[(String, Long, Double), (String, Double, Double)] {

    // 定义状态，最后的温度状态
    private var lastTempState: ValueState[Double] = _

    //初始化
    override def open(parameters: Configuration): Unit = {
      // 创建状态描述
      val lastTempDescriptor = new ValueStateDescriptor[Double]("lastTemp", classOf[Double])
      // 获得状态对象
      lastTempState = getRuntimeContext.getState[Double](lastTempDescriptor)
    }

    //flatMap操作，判断温差，记录最后的温度
    override def flatMap(sensor: (String, Long, Double), out: Collector[(String, Double, Double)]): Unit = {

      // 获得最后的温度
      val lastTemp = lastTempState.value()
      // 计算温差
      val tempDiff = (sensor._3 - lastTemp).abs
      //如果温差超过阈值
      if (tempDiff > threshold) {
        // 输出（传感器ID，温度，温差）
        out.collect((sensor._1, sensor._3, tempDiff))
      }
      //更新状态
      println("update temp:"+sensor._3)
      this.lastTempState.update(sensor._3)
    }

  }

}
