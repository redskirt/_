package chapter7

import org.apache.flink.cep.functions.{PatternProcessFunction, TimedOutPartialMatchHandler}
import org.apache.flink.cep.scala.CEP
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector

import java.util



object OrderTimeoutDetect {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // 读取数据源
    val orderEventStream = env.fromElements(
      OrderEvent("zhangsan", "order1", "create", 1*1000L),
      OrderEvent("lisi", "order2", "create", 2*1000L),
      OrderEvent("zhangsan", "order1", "modify", 10 * 1000L),
      OrderEvent("zhangsan", "order1", "pay", 60 * 1000L),
      OrderEvent("lisi", "order3", "create", 10 * 60 * 1000L),
      OrderEvent("lisi", "order3", "pay", 20 * 60 * 1000L)
    ).assignAscendingTimestamps(_.timestamp)
      //按照订单分组
      .keyBy(_.orderId)

    // 定义检测的模式
    val pattern = Pattern.begin[OrderEvent]("create")
      //从类型为create的订单开始
      .where(_.eventType == "create")
      //已经支付的订单
      .followedBy("pay").
      where(_.eventType == "pay")
      .within(Time.minutes(15))

    // 将模式应用到事件流上
    val patternStream = CEP.pattern(orderEventStream, pattern)

    // 检测匹配事件和部分匹配的超时事件
    val payedOrderStream = patternStream.process(new OrderPayDetect())
    //输出侧输出流
    payedOrderStream.getSideOutput(new OutputTag[String]("timeout")).print("timeout")
    //输出正常支付流
    payedOrderStream.print("pay")
    //开始执行
    env.execute()
  }
  //订单检测处理类
  class OrderPayDetect extends PatternProcessFunction[OrderEvent, String] with TimedOutPartialMatchHandler[OrderEvent]{
    override def processMatch(map: util.Map[String, util.List[OrderEvent]], context: PatternProcessFunction.Context, collector: Collector[String]): Unit = {
      // 处理正常支付的匹配事件
      val payEvent = map.get("pay").get(0)
      collector.collect(s"${payEvent.userId}的订单${payEvent.orderId}已支付")
    }
    override def processTimedOutMatch(map: util.Map[String, util.List[OrderEvent]], context: PatternProcessFunction.Context): Unit = {
      // 处理部分匹配的超时事件
      val createEvent = map.get("create").get(0)
      context.output(new OutputTag[String]("timeout"), s"${createEvent.userId}的订单${createEvent.orderId}超时未支付")
    }
  }

}
