package chapter6

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment


object TableTest6 {
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
    // 创建表
    tableEnv.executeSql("CREATE TABLE page_view (" +
      " id INT," +
      " ts INT," +
      " user_id INT," +
      " visit_url STRING," +
      " visit_time INT" +
      ") WITH (" +
      " 'connector' = 'filesystem'," +
      " 'path' = 'data/pageview.csv'," +
      " 'format' = 'csv'" +
      ") ")

    // 进行分组聚合统计
    val table1 = tableEnv.sqlQuery("select user_id " +
      ",sum(visit_time) as visit_time_sum " +
      ",min(visit_time) as visit_time_min " +
      ",max(visit_time) as visit_time_max " +
      ",avg(visit_time) as visit_time_avg " +
      ",count(id) as record_count " +
      "from page_view " +
      "group by user_id")
    // 创建临时表
    tableEnv.createTemporaryView("visit_time_sum_table", table1)
    //输出schema
    table1.printSchema()
    //转换为数据流打印输出
    tableEnv.toChangelogStream(table1).print("result1")
    //开始执行
    env.execute()
  }
}
