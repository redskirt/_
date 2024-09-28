package chapter8

import chapter3.SensorSource
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer

object KafkaTest2 {
  def main(args: Array[String]): Unit = {

    // 获取运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //数据源
    val stream = env.addSource(new SensorSource(5))
    //转换为字符串
    val stream2 = stream.map(sensor => sensor._1 + "," + sensor._2 + "," + sensor._3)
    //Kafka的连接
    val brokerList = "hadoop1:9092"
    //Kafka的主题
    val topic = "flink-kafka-sink"
    /// 将数据写入到kafka
    stream2.addSink(new FlinkKafkaProducer[String](brokerList, topic, new SimpleStringSchema()))
    //输出数据流
    stream2.print()
    //开始执行
    env.execute()
  }
}
