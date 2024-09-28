package chapter8

import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

import java.util.Properties


object KafkaTest4 {
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
    val sourceTopic = "flink-kafka-score"
    //添加数据源
    val stream: DataStream[String] = env.addSource(new FlinkKafkaConsumer[String](sourceTopic,
      new SimpleStringSchema(), properties))
    //转换为UserScore对象
    val stream2=stream.map(json=>{
      val jsonObj: JSONObject = JSON.parseObject(json)
      //用户
      val user: String = jsonObj.getString("user")
      //课程
      val course: String = jsonObj.getString("course")
      //分数
      val score: Int = jsonObj.getIntValue("score")
      //构造对象
      UserScore(user,course,score)
    })
    // 创建表环境
    val tableEnv = StreamTableEnvironment.create(env)
    // 将DataStream转换成表
    val table:Table = tableEnv.fromDataStream(stream2)
    //创建临时表
    tableEnv.createTemporaryView("user_score", table)
    //计算每个学生的平均分
    val table2 = tableEnv.sqlQuery("select user,avg(score) " +
      "from user_score " +
      "group by user ")
    // 转换成流打印输出
    tableEnv.toChangelogStream(table2).print()
    //开始执行
    env.execute()
  }

  /**
   * 用户分数
   * @param user 用户
   * @param course 课程
   * @param score 分数
   */
  case class UserScore(user: String,course:String, score: Int)
}
