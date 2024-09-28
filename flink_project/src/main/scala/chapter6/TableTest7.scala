package chapter6

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment


object TableTest7 {
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

    // 创建page_view表
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
    // 创建user_info表
    tableEnv.executeSql("CREATE TABLE user_info (" +
      " user_id INT," +
      " name STRING," +
      " birthday STRING" +
      ") WITH (" +
      " 'connector' = 'filesystem'," +
      " 'path' = 'data/user.csv'," +
      " 'format' = 'csv'" +
      ") ")
   //关联表
    val table1 = tableEnv.sqlQuery("select page_view.user_id, user_info.name,page_view.visit_time " +
      "from page_view " +
      "left join user_info " +
      "on page_view.user_id=user_info.user_id")
    //输出schema
    table1.printSchema()
    // 结果打印输出
    tableEnv.toChangelogStream(table1).print()
    //开始执行
    env.execute()
  }
}
