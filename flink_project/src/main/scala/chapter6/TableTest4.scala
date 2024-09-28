package chapter6

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.functions.ScalarFunction


object TableTest4 {
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

    // 注册标量函数
    tableEnv.createTemporarySystemFunction("isIndex", classOf[isIndex])
    // 调用函数进行查询转换
    val table1 = tableEnv.sqlQuery("select user_id,visit_url,isIndex(visit_url) as is_index " +
      "from page_view")
    //查看schema
    table1.printSchema()
    // 打印输出
    tableEnv.toDataStream(table1).print()
    //执行
    env.execute()
  }
  // 实现自定义的标量函数,访问index.html 返回1 否则返回0
  class isIndex extends ScalarFunction {
    def eval(visitUrl: String): Int = {
      if(visitUrl.contains("index.html")) 1 else 0
    }
  }
}
