package chapter8

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}

import java.util.Properties



object KafkaTest3 {
  def main(args: Array[String]): Unit = {
    //创建运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //设置属性
    val properties = new Properties()
    //连接Kafka Source的地址
    properties.setProperty("bootstrap.servers", "hadoop1:9092")
    //消费者组名称
    properties.setProperty("group.id", "group1")
    //定义Kafka Source主题
    val sourceTopic = "flink-kafka-source"
    //添加数据源
    val stream: DataStream[String] = env.addSource(new FlinkKafkaConsumer[String](sourceTopic,
      new SimpleStringSchema(), properties))
    //转换为3元组
    val stream2: DataStream[(Int, String, Int)] = stream.map(pageView => {
      val fields = pageView.split(",")
      (fields(0).trim.toInt, fields(1).trim, fields(2).trim.toInt)
    })
    //按照访问时间过滤，只保留访问时间>10的数据
    val stream3:DataStream[(Int, String, Int)]  = stream2.filter(_._3 > 10)
    //三元组转换为字符串
    val stream4:DataStream[String]=stream3.map(pageView=>pageView._1+","+pageView._2+","+pageView._3)
    //Kafka Sink的连接
    val brokerList = "hadoop1:9092"
    //Kafka Sink的主题
    val sinkTopic = "flink-kafka-sink"
    /// 将数据写入到kafka
    stream4.addSink(new FlinkKafkaProducer[String](brokerList, sinkTopic, new SimpleStringSchema()))
    //开始执行
    env.execute()
  }
}
