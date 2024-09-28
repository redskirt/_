package chapter3

import org.apache.flink.api.common.functions.FilterFunction
import org.apache.flink.streaming.api.scala._

/**
 * 转换算子
 */
object TransTest6 {
  def main(args: Array[String]): Unit = {
    // 获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //从集合中读取数据
    val dataStream: DataStream[PageView] = env.fromCollection(List(
      PageView(1, 1547718100, 1, "/index.html", 10),
      PageView(2, 1547718200, 2, "/index.html", 20),
      PageView(3, 1547719300, 3, "/index.html", 10),
      PageView(4, 1547720300, 1, "/goods.html", 100),
      PageView(5, 1547720600, 2, "/cart.html", 30)
    ))
    val dataStream2 = dataStream.filter(new MyFilterFunc(10))
    //输出结果
    dataStream2.print()
    //开始执行
    env.execute()
  }

  /**
   * 按照访问时间进行过滤，过滤掉小于指定时间的记录
   *
   * @param minVisitTime 最小时间
   */
  class MyFilterFunc(minVisitTime: Int) extends FilterFunction[PageView] {
    /**
     * 保留访问时间超过最小时间的记录
     *
     * @param pageView PageView对象
     * @return 访问时间是否超过最小时间
     */
    override def filter(pageView: PageView): Boolean = pageView.visitTime > minVisitTime
  }

}
