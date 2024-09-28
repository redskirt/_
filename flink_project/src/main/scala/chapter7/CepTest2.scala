package chapter7

import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.cep.functions.PatternProcessFunction
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.cep.scala.{CEP, PatternStream}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector

import java.time.Duration
import java.util


object CepTest2 {
  def main(args: Array[String]): Unit = {
    //创建运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    // 读取数据源
    val loginEventStream = env.fromElements(
      LoginEvent("zhangsan", "192.168.0.1", "fail", 2000L),
      LoginEvent("zhangsan", "192.168.0.2", "fail", 3000L),
      LoginEvent("lisi", "192.168.1.9", "fail", 4000L),
      LoginEvent("zhangsan", "192.168.1.10", "fail", 5000L),
      LoginEvent("lisi", "192.168.1.9", "fail", 7000L),
      LoginEvent("lisi", "192.168.1.9", "fail", 8000L),
      LoginEvent("lisi", "192.168.1.9", "success", 6000L)
    ).assignTimestampsAndWatermarks(WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(2))
      .withTimestampAssigner(new SerializableTimestampAssigner[LoginEvent] {
        override def extractTimestamp(t: LoginEvent, l: Long): Long = t.timestamp
      }))
    // 定义Pattern，检测连续2-3次登录失败事件
    val pattern = Pattern.begin[LoginEvent]("fail")
      .where(_.eventType == "fail").times(2,4).consecutive()
      .within(Time.seconds(5))
    // 将模式应用到事件流上，检测匹配的复杂事件
    val patternStream: PatternStream[LoginEvent] = CEP.pattern(loginEventStream.keyBy(_.userId), pattern)
    // 将检测到的匹配事件报警输出
    val resultStream: DataStream[String] = patternStream.process(new PatternProcessFunction[LoginEvent, String] {
      override def processMatch(map: util.Map[String, util.List[LoginEvent]], context: PatternProcessFunction.Context, collector: Collector[String]): Unit = {
        val eventList=map.get("fail")
        // 获取匹配到的复杂事件
        val firstFail = eventList.get(0)
        val secondFail = eventList.get(1)
        if(eventList.size>2){
          val thirdFail = eventList.get(2)
          collector.collect(s"${firstFail.userId} 连续3次登录失败！时间：${firstFail.timestamp}, ${secondFail.timestamp}, ${thirdFail.timestamp}")
        }else{
          collector.collect(s"${firstFail.userId} 连续2次登录失败！时间：${firstFail.timestamp}, ${secondFail.timestamp}")
        }
      }
    })
    //输出流
    resultStream.print()
    //开始执行
    env.execute()
  }

}
