package chapter3

import org.apache.flink.streaming.api.scala._

/**
 * 转换算子
 */
object TransTest3 {
  def main(args: Array[String]): Unit = {
    // 获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //从集合中读取数据
    val dataStream: DataStream[String] = env.fromCollection(List(
     "hello world hello flink",
     "hello scala"
    ))
    //过滤访问index.html页面的记录
    //val dataStream2=dataStream.flatMap(line=>line.split(" "))
    val dataStream2=dataStream.flatMap(_.split(" "))
    //控制台输出
    dataStream2.print()
    //开始执行
    env.execute()
  }
}
