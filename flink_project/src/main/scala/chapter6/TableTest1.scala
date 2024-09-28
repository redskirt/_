package chapter6

import chapter3.PageView
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.Expressions.$
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment


object TableTest1 {
  def main(args: Array[String]): Unit = {
    //获取运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    // 数据源
    val dataStream: DataStream[PageView] = env.fromCollection(List(
      PageView(1, 1547718100, 1, "/index.html", 10),
      PageView(2, 1547718200, 2, "/index.html", 20),
      PageView(3, 1547719300, 3, "/index.html", 10),
      PageView(4, 1547720300, 1, "/goods.html", 100),
      PageView(5, 1547720600, 2, "/cart.html", 30)
    ))
    // 创建表环境
    val tableEnv = StreamTableEnvironment.create(env)
    // 将DataStream转换成表
    val pvTable = tableEnv.fromDataStream(dataStream)
    //创建临时表
    tableEnv.createTemporaryView("page_view", pvTable)

    // 只查询访问过index.html的记录
    val resultTable1 = pvTable.select($("userId"), $("visitUrl"), $("visitTime"))
      .where($("visitUrl").isEqual("/index.html"))
    // 转换成流打印输出
    tableEnv.toDataStream(resultTable1)
      .print("resultTable1")

    // 直接执行SQL
    val resultTable2 = tableEnv.sqlQuery("select userId,visitUrl,visitTime " +
      "from page_view " +
      "where visitUrl = '/index.html' ")
    // 转换成流打印输出
    tableEnv.toDataStream(resultTable2)
      .print("resultTable2")

    //开始执行
    env.execute()
  }
}
