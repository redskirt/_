package chapter5

import chapter3.SensorSource
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.runtime.state.StateBackend
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.runtime.state.memory.MemoryStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.scala._

/**
 * 状态后端
 *
 *
 *
 */
object StateendTest {
  def main(args: Array[String]): Unit = {
    //获取环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    // 内存状态后端
    val stateBackend:MemoryStateBackend =new MemoryStateBackend()
    // 文件系统状态后端
    //val stateBackend =new FsStateBackend("data/state/")
    //RocksDB状态后端
   // val stateBackend: StateBackend = new RocksDBStateBackend("file:///IdeaProjects/flink_project/data/rocksdb", true)
   env.setStateBackend(stateBackend)
    // 其它配置
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    env.getCheckpointConfig.setCheckpointTimeout(30000L)
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(2)
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500L)

     //重启策略的配置
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 10000L))

    // 数据源
    val dataStream: DataStream[(String, Long, Double)] = env
      .addSource(new SensorSource(10))
    val warningStream: DataStream[(String, Double, Double)] = dataStream
      .keyBy(_._1)
      .flatMapWithState[(String, Double, Double), Double]({
      case (inputData: (String,Long,Double), None) => (List.empty, Some(inputData._3))
      case (inputData: (String,Long,Double), lastTemp: Some[Double]) => {
        val diff = (inputData._3 - lastTemp.get).abs
        if( diff > 10.0 ){
          ( List( (inputData._1, lastTemp.get, inputData._3) ), Some(inputData._3) )
        } else {
          (List.empty, Some(inputData._3))
        }
      }
    })

    warningStream.print()
    //开始执行
    env.execute()
  }
}
