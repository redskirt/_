package chapter8

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

import java.util.Properties


object KafkaTest1 {
  def main(args: Array[String]): Unit = {
    //创建运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //设置属性
    val properties = new Properties()
    //连接Kafka的地址
    properties.setProperty("bootstrap.servers", "hadoop1:9092")
    //消费者组名称
    properties.setProperty("group.id", "group1")
   //定义Kafka主题
    val topic="flink-kafka-source"
    //添加数据源
    val stream: DataStream[String] = env.addSource(new FlinkKafkaConsumer[String](topic,
      new SimpleStringSchema(), properties))
    //输出流
    stream.print()
    //开始执行
    env.execute()
  }
}
