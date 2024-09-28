package chapter3

import org.apache.flink.streaming.api.scala._

/**
 * 数据源测试
 */
object SourceTest1 {
  def main(args: Array[String]): Unit = {
    // 获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //  从集合中读取数据
    val dataStream: DataStream[PageView] = env.fromCollection(List(
      PageView(1, 1547718100, 1,"/index.html",10),
      PageView(2, 1547718200, 2,"/index.html",20),
      PageView(3, 1547719300, 3,"/index.html",10),
      PageView(4, 1547720300, 1,"/goods.html",100),
      PageView(5, 1547720600, 2,"/cart.html",30)
    ))
    //输出数据流
    dataStream.print()
    //开始执行
    env.execute()
  }
}
