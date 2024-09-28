package chapter6


import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.functions.AggregateFunction


object TableTest5 {
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

    // 注册聚合函数
    tableEnv.createTemporarySystemFunction("myAvg", classOf[MyAvg])

    // 调用函数进行查询转换
    val resultTable = tableEnv.sqlQuery("select user_id, myAvg(visit_time, 1) as avg_visit_time " +
      "from page_view " +
      "group by user_id ")

    //结果打印输出
    tableEnv.toChangelogStream(resultTable).print()
    //执行
    env.execute()
  }

  /**
   * 聚合结果样例类
   *
   * @param sum   时间汇总
   * @param count 数量
   */
  case class MyAvgAccumulator(var sum: Long = 0, var count: Int = 0)

  // 实现自定义的聚合函数，计算加权平均数
  class MyAvg extends AggregateFunction[java.lang.Long, MyAvgAccumulator] {
    override def getValue(acc: MyAvgAccumulator): java.lang.Long = {
      if (acc.count == 0) {
        null
      } else {
        acc.sum / acc.count
      }
    }

    // 创建累加器
    override def createAccumulator(): MyAvgAccumulator = MyAvgAccumulator()

    // 每收到一条数据，都会调用方法
    def accumulate(acc: MyAvgAccumulator, iValue: java.lang.Long, iWeight: Int): Unit = {
      acc.sum += iValue
      acc.count += iWeight
    }
  }
}
