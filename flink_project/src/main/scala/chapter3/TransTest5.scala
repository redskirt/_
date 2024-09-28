package chapter3

import org.apache.flink.streaming.api.scala._

/**
 * 转换算子
 */
object TransTest5 {
  def main(args: Array[String]): Unit = {
    // 获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    val dataStream = env.fromCollection(List(
      ("sensor_1", 35.8),
      ("sensor_2", 15.4),
      ("sensor_2", 16.7),
      ("sensor_2", 19.2),
      ("sensor_1", 36.9)
    ))
    //统计每个传感器温度的最低值
    val keyedStream = dataStream.keyBy(0)
    val dataStream2= keyedStream
      .reduce((sensorData, newSensorData) =>{
        //println("sensor1:"+sensorData._2+ " vs sensor2: "+sensorData._2)
        (sensorData._1, sensorData._2.min(sensorData._2))
      })
    //输出结果
    dataStream2.print()
    //开始执行
    env.execute()
  }
}
