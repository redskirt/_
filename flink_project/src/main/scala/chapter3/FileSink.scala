package chapter3

import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.apache.flink.streaming.api.scala._
/**
 * 输出到文件
 */
object FileSink {
  def main(args: Array[String]): Unit = {
    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(2)
    //  从集合中读取数据
    val dataStream: DataStream[PageView] = env.fromCollection(List(
      PageView(1, 1547718100, 1, "/index.html", 10),
      PageView(2, 1547718200, 2, "/index.html", 20),
      PageView(3, 1547719300, 3, "/index.html", 10),
      PageView(4, 1547720300, 1, "/goods.html", 100),
      PageView(5, 1547720600, 2, "/cart.html", 30)
    ))
    // 以文本形式写入到文件中
    val fileSink = StreamingFileSink
      .forRowFormat(new Path("./output"),
        new SimpleStringEncoder[String]("UTF-8"))
      .build()
    dataStream.map(_.toString).addSink( fileSink )
    //开始执行
    env.execute()
  }
}
