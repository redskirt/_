package chapter6

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment


object TableTest3 {
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
    //标量函数
    val table1 = tableEnv.sqlQuery("select user_id,visit_url,upper(visit_url) " +
      "from page_view")
    //聚合函数
    val table2 = tableEnv.sqlQuery("select user_id,avg(visit_time) " +
      "from page_view " +
      "group by user_id")
    // 打印输出
    tableEnv.toDataStream(table1).print("result1")
    tableEnv.toChangelogStream(table2).print("result2")
    //执行
    env.execute()
  }

}
