package chapter1

import chapter7.LoginEvent
import org.apache.flink.cep.PatternSelectFunction
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.cep.scala.{CEP, PatternStream}
import org.apache.flink.streaming.api.scala._

import java.util


object CepTest1 {
  def main(args: Array[String]): Unit = {
    //创建运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //读取数据源
    val loginEventStream = env.fromElements(
      LoginEvent("zhangsan", "192.168.1.1", "fail", 2000L),
      LoginEvent("zhangsan", "192.168.1.2", "fail", 3000L),
      LoginEvent("lisi", "192.168.1.1", "fail", 4000L),
      LoginEvent("zhangsan", "192.168.1.5", "fail", 5000L),
      LoginEvent("lisi", "192.168.1.9", "success", 6000L),
      LoginEvent("lisi", "192.168.1.8", "fail", 7000L),
      LoginEvent("lisi", "192.168.1.2", "fail", 8000L)
    ).assignAscendingTimestamps(_.timestamp)

    // 定义Pattern，检测连续3次登录失败事件
    val pattern = Pattern.begin[LoginEvent]("firstLoginFail").where(_.eventType == "fail") // 第1次登录失败事件
      .next("secondLoginFail").where(_.eventType == "fail") // 第2次登录失败事件
      .next("thirdLoginFail").where(_.eventType == "fail") // 第3次登录失败事件
    // 将模式应用到事件流上，检测匹配的复杂事件
    val patternStream: PatternStream[LoginEvent] = CEP.pattern(loginEventStream.keyBy(_.userId), pattern)
    // 将检测到的匹配事件报警输出
    val resultStream: DataStream[String] = patternStream.select(new PatternSelectFunction[LoginEvent, String] {
      override def select(map: util.Map[String, util.List[LoginEvent]]): String = {
        // 获取匹配到的复杂事件
        val firstFail = map.get("firstLoginFail").get(0)
        val secondFail = map.get("secondLoginFail").get(0)
        val thirdFail = map.get("thirdLoginFail").get(0)
        // 返回报警信息
        s"${firstFail.userId}连续3次登录失败！时间：" +
          s" ${firstFail.timestamp}" +
          s", ${secondFail.timestamp}" +
          s", ${thirdFail.timestamp}"
      }
    })
    //输出流
    resultStream.print()
    //开始执行
    env.execute()
  }
}
