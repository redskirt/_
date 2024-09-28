package chapter3

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala._
/**
 * 转换算子
 */
object TransTest7 {
  def main(args: Array[String]): Unit = {
    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(2)
    //从集合中读取数据
    val dataStream: DataStream[PageView] = env.fromCollection(List(
      PageView(1, 1547718100, 1, "/index.html", 10),
      PageView(2, 1547718200, 2, "/index.html", 20),
      PageView(3, 1547719300, 3, "/index.html", 10),
      PageView(4, 1547720300, 1, "/goods.html", 100),
      PageView(5, 1547720600, 2, "/cart.html", 30)
    ))
    //过滤访问index.html页面的记录
    val dataStream2 = dataStream.map(new MyRichMap())
    //控制台输出
    dataStream2.print()
    //开始执行
    env.execute()
  }

  /**
   * 自定义map函数
   */
  class MyRichMap() extends RichMapFunction[PageView, (Int, String, Int)] {
    /**
     * 任务打开
     * @param parameters
     */
    override def open(parameters: Configuration): Unit = {
      println("index:" + getRuntimeContext.getIndexOfThisSubtask + "  start")
    }

    /**
     * 执行map
     * @param in PageView对象
     * @return 三元组
     */
    override def map(in: PageView) = (in.userId, in.visitUrl, in.visitTime)

    /**
     * 任务关闭
     */
    override def close(): Unit = {
      println("index:" + getRuntimeContext.getIndexOfThisSubtask + " end")
    }
  }

}
