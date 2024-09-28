package chapter5

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object EnvSet {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //执行检查点的时间间隔
    env.enableCheckpointing(1000)
    //指定状态后端
    env.setStateBackend(new FsStateBackend("file:///C://checkpoint"))
    //设置模式为精确一次(默认值)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    //设置两次检查点的最小时间间隔
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500)
    //设置可容忍的失败的检查点数量，默认值为0，不容忍任何失败
    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    //设置检查点的超时时间
    env.getCheckpointConfig.setCheckpointTimeout(6000)
    //重启策略：固定延迟重启。程序出现异常的时候重启2次，每次时间间隔3秒，超过2次程序仍然出现异常则退出
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(2, 3000))
    println("set env finish")
  }

}
