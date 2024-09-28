package chapter4

import org.apache.flink.streaming.api.scala._

/**
 * 按照事件数量进行统计，最高温度
 */
object WindowTest1 {

  def main(args: Array[String]): Unit = {
    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    val inputDataStream= env.fromCollection(List(
      ("sensor_1", 1547718199, 1.0),
      ("sensor_1", 1547718201, 2.0),
      ("sensor_2", 1547718202, 3.0),
      ("sensor_1", 1547718199, 4.0),
      ("sensor_1", 1547718201, 5.0),
      ("sensor_1", 1547718202, 6.0)
    ))
    //行转换为（传感器ID，温度）
    val dataStream = inputDataStream
      .map(data => {
           (data._1, data._3)
      })
    //根据传感器ID分组
    val dataStream2 = dataStream.keyBy(_._1)
      // 滚动窗口
      .countWindow(3)
      //按照温度汇总
      .max(1)
    //输出流
    dataStream2.print()
    //开始执行
    env.execute()
  }
}

