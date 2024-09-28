package chapter5

import chapter3.PageView
import org.apache.flink.streaming.api.scala._

/**
 * 分流
 */
object SplitStreamTest1 {
  def main(args: Array[String]): Unit = {

    //创建运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    //从集合中读取数据
    val dataStream: DataStream[PageView] = env.fromCollection(List(
      PageView(1, 1547718100, 1, "/index.html", 10),
      PageView(2, 1547718200, 2, "/index.html", 20),
      PageView(3, 1547719300, 3, "/index.html", 8),
      PageView(4, 1547720300, 1, "/goods.html", 100),
      PageView(5, 1547720600, 2, "/cart.html", 30)
    ))
    //停留时间短的流
    val stream1=dataStream.filter(_.visitTime<=10)
    //停留时间长的流
    val stream2=dataStream.filter(_.visitTime>=100)
    //输出低温流
    stream1.print("stream1")
    //输出正常流
    stream2.print("stream2")
    //开始执行
    env.execute()
  }
}

