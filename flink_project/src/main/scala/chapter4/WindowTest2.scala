package chapter4

import org.apache.flink.streaming.api.scala._

/**
 * 按照事件数量进行统计，最高温度
 *
 */
object WindowTest2 {
  def main(args: Array[String]): Unit = {
    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)

    val inputDataStream= env.fromCollection(List(
      ("sensor_1", 1547718199, 1.0),
      ("sensor_1", 1547718201, 2.0),
      ("sensor_1", 1547718202, 3.0),
      ("sensor_1", 1547718199, 4.0),
      ("sensor_1", 1547718201, 5.0),
      ("sensor_1", 1547718202, 6.0),
      ("sensor_2", 1547718199, 1.0),
      ("sensor_2", 1547718201, 2.0),
      ("sensor_2", 1547718202, 3.0),
      ("sensor_2", 1547718199, 4.0),
      ("sensor_2", 1547718201, 5.0)
    ))
    //行转换为（传感器ID，温度）
    val dataStream: DataStream[(String, Double)] = inputDataStream
      .map(data => {
           (data._1, data._3)
      })
    //根据传感器ID分组
    val dataStream2: DataStream[(String, Double)] = dataStream.keyBy(_._1)
      // 滑动窗口
       .countWindow(4,2)
      //增量聚合
      .reduce((sensor, newSensor) =>{
        (sensor._1, sensor._2.max(newSensor._2))
      })
    //输出流
    dataStream2.print()
    //开始执行
    env.execute()
  }
}

