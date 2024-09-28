package chapter5

import chapter3.SensorSource
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.scala._
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector

import java.time.Duration
import java.util
import scala.collection.JavaConverters._

object HighTempCount {

  def main(args: Array[String]) {

    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置检查点时间间隔
    env.getCheckpointConfig.setCheckpointInterval(1 * 1000)
    //设置并行度
    env.setParallelism(1)
    //执行检查点的时间间隔
    env.enableCheckpointing(1000)
    //指定状态后端
    env.setStateBackend(new FsStateBackend("file:///C://checkpoint"))
    //设置两次检查点的最小时间间隔
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500)
    //设置可容忍的失败的检查点数量，默认值为0，不容忍任何失败
    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    //设置检查点的超时时间
    env.getCheckpointConfig.setCheckpointTimeout(6000)
    //重启策略：固定延迟重启。程序出现异常的时候重启2次，每次时间间隔3秒，超过2次程序仍然出现异常则退出
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(2, 3000))

    // 数据源
    val dataStream: DataStream[(String, Long, Double)] = env
      .addSource(new SensorSource(10))
      // 分配时间和水位线
      .assignTimestampsAndWatermarks(
        //指定水位线生成策略:周期性策略
        WatermarkStrategy.forBoundedOutOfOrderness[(String, Long, Double)](
          Duration.ofSeconds(3))
          .withTimestampAssigner(new SerializableTimestampAssigner[(String, Long, Double)] {
            //指定事件时间戳
            override def extractTimestamp(element: (String, Long, Double), recordTimestamp: Long): Long = element._2
          })
      )
    //（任务ID，高温数量）
    val dataStream2: DataStream[(Int, Long)] = dataStream.flatMap(new HighTempCounterOpState(20.0))
    // 控制台输出
    dataStream2.print()
    // 开始执行
    env.execute()
  }

  /**
   * 计算每个并行任务出现的高温数量
   *
   * @param threshold 温度阈值
   */
  class HighTempCounterOpState(val threshold: Double)
    extends RichFlatMapFunction[(String, Long, Double), (Int, Long)]
      with ListCheckpointed[java.lang.Long] {
    // 并行任务的索引
    private lazy val subtaskIdx = getRuntimeContext.getIndexOfThisSubtask
    // 高温数量
    private var highTempCount = 0L

    //实现更新逻辑
    override def flatMap(in: (String, Long, Double), out: Collector[(Int, Long)]): Unit = {
      if (in._3 > threshold) {
        // 出现高温，数量增加
        highTempCount += 1
        // 更新：（任务索引，高温数量）
        out.collect((subtaskIdx, highTempCount))
      }
    }

    //保存状态
    override def restoreState(state: util.List[java.lang.Long]): Unit = {
      highTempCount = 0
      // 添加数量
      for (cnt <- state.asScala) {
        highTempCount += cnt
      }
      println("restoreState:" + highTempCount)
    }

    //状态快照
    override def snapshotState(checkpointId: Long, ts: Long): java.util.List[java.lang.Long] = {
      // 状态
      java.util.Collections.singletonList(highTempCount)
    }
  }
}