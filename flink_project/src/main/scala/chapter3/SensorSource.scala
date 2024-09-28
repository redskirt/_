package chapter3

import org.apache.flink.streaming.api.functions.source.SourceFunction
import scala.util.Random

/**
 * 自定义传感器数据源
 * 返回元组（传感器ID，时间戳，温度）
 *
 */
class SensorSource(second:Int) extends SourceFunction[(String,Long,Double)] {
  // 定义一个flag，表示数据源是否正常运行
  var running: Boolean = true
  //取消发送
  override def cancel(): Unit = running = false
  // 随机生成 SensorReading数据
  override def run(ctx: SourceFunction.SourceContext[(String,Long,Double)]): Unit = {
    // 定义一个随机数发生器
    val rand = new Random()
    // 随机生成 10个传感器的温度值，并且不停在之前温度基础上更新（随机上下波动）
    var curTemps = 1.to(10).map(
      i => ("sensor_" + i, 60 + rand.nextGaussian() * 20)
    )
    //无限循环，生成随机数据流
    while (running) {
      // 在当前温度基础上，随机生成微小波动
      curTemps = curTemps.map(
        data => (data._1, data._2 + rand.nextGaussian())
      )
      //定义随机的时间戳
      val curTs = System.currentTimeMillis()-rand.nextInt(20)*1000
      curTemps.foreach(
        data => {
          //收集数据并发送
          ctx.collect((data._1, curTs, data._2))
        }
      )
      //定义间隔时间
      Thread.sleep(1000L*second)
    }
  }
}