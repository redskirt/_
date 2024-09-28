package chapter6

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.Expressions.$
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

object TableTest2 {
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
    // 执行SQL创建表
    tableEnv.executeSql("CREATE TABLE page_view (" +
      " id STRING," +
      " ts BIGINT," +
      " user_id INT," +
      " visit_url STRING," +
      " visit_time INT" +
      ") WITH (" +
      " 'connector' = 'filesystem'," +
      " 'path' = 'data/pageview.csv'," +
      " 'format' = 'csv'" +
      ") ")
    //获取Table对象
    val pvTable = tableEnv.from("page_view")
    //查看schema
    pvTable.printSchema()
    //使用Table API过滤数据
    val resultTable = pvTable
      .where($("visit_url").isEqual("/index.html"))
      .select($("user_id"), $("visit_time"))
    //使用SQL创建表page_view_result
    tableEnv.executeSql("CREATE TABLE page_view_result (" +
      " user_id INT," +
      " visit_time INT" +
      ") WITH (" +
      " 'connector' = 'filesystem'," +
      " 'path' = 'data/sum_page_view.csv'," +
      " 'format' = 'csv'" +
      ") ")
    // 将结果表写入输出表中
    resultTable
      .executeInsert("page_view_result")
    //输出
    tableEnv.toChangelogStream(resultTable)
      .print("resultTable1")
    // 转换成流打印输出
    tableEnv.toDataStream(resultTable)
      .print("resultTable2")
    //开始执行
    env.execute()
  }
}