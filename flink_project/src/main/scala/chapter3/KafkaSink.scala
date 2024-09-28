package chapter3

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011


/**
 * Kafka Sink
 *  --启动kafka
 *  kafka-topics.sh --zookeeper localhost:2181 --create --topic flink-kafka-sink --replication-factor 3 --partitions 2
 *  --启动控制台消费者
 * kafka-console-consumer.sh --zookeeper hadoop1:2181 --topic flink-kafka-sink
 */
object KafkaSink {
  def main(args: Array[String]): Unit = {


    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //添加数据源
    val inputStream:DataStream[(String,Long,Double)] = env.addSource(new SensorSource(5))
    //转换数据源，map 数据格式： 传感器的ID:传感器的温度
    val dataStream: DataStream[String] = inputStream
      .map(sensorReading => {
        sensorReading._1+":"+sensorReading._2
      })
    //添加Sink Kafka
    val brokerList="hadoop1:9092,hadoop2:9092"
    //主题
    val topic="flink-kafka-sink"
    //添加Sink
   // dataStream.addSink(new FlinkKafkaProducer011[String](brokerList, topic, new SimpleStringSchema()))
    //开始执行
    env.execute()
  }
}
