package chapter6

import chapter3.PageView
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.Expressions.$
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

import java.time.Duration


object TableTest8 {
  def main(args: Array[String]): Unit = {
    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    // 环境的配置参数
    val settings = EnvironmentSettings.newInstance()
      .inStreamingMode()
      .useBlinkPlanner()
      .build()
    //创建表环境
    val tableEnv = StreamTableEnvironment.create(env, settings)
    //创建数据流
    val dataStream = env.fromElements(
      PageView(1, 1547718100, 1, "/index.html", 10),
      PageView(2, 1547718200, 2, "/index.html", 20),
      PageView(3, 1547719300, 3, "/index.html", 10),
      PageView(4, 1547720300, 1, "/goods.html", 100),
      PageView(5, 1547720600, 2, "/cart.html", 30)
    ).assignTimestampsAndWatermarks(WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(2))
      .withTimestampAssigner(new SerializableTimestampAssigner[PageView] {
        override def extractTimestamp(t: PageView, l: Long): Long = t.timestamp
      }))
    // 将DataStream转换成表
    val pvTable = tableEnv.fromDataStream(dataStream, $("visitUrl"), $("userId"),
      $("timestamp").as("ts"), $("pv_ts").rowtime())
    //创建临时表
    tableEnv.createTemporaryView("pvTable", pvTable)
    // 进行窗口聚合统计，计算每个用户目前的访问量
    val urlCountWindowTable = tableEnv.sqlQuery(
      """
        |SELECT userId, COUNT(visitUrl) AS cnt, window_start, window_end
        |FROM TABLE (
        |  TUMBLE(TABLE pvTable, DESCRIPTOR(pv_ts), INTERVAL '5' SECOND)
        |)
        |GROUP BY userId, window_start, window_end
        |""".stripMargin)

    //输出结果
    tableEnv.toDataStream(urlCountWindowTable).print()
    //执行
    env.execute()
  }
}
