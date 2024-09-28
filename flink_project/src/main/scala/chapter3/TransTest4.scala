package chapter3

import org.apache.flink.streaming.api.scala._

/**
 * 转换算子
 */
object TransTest4 {
  def main(args: Array[String]): Unit = {
    // 获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //从集合中读取数据
    val dataStream = env.fromCollection(List(
      (1, 1547718100, 1, "/index.html", 10),
      (2, 1547718200, 2, "/index.html", 20),
      (3, 1547719300, 3, "/index.html", 10),
      (4, 1547720300, 1, "/goods.html", 100),
      (5, 1547720600, 2, "/cart.html", 30)
    ))
    //转换为二元组（用户ID，访问时间）
    val keyedStream = dataStream.map(pageView => (pageView._3, pageView._5))
      //按照用户ID进行分组，按照访问时间进行汇总
      .keyBy(_._1).sum("_2")
    //输出结果
    keyedStream.print()
    //开始执行
    env.execute()
  }
}
