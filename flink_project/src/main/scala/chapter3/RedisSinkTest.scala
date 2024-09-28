package chapter3

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

/**
 * 输出到Redis
 */
object RedisSinkTest {
  def main(args: Array[String]): Unit = {
    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //自定义数据源
    val dataStream = env.addSource(new SensorSource(10))
    // 创建一个Jedis连接的配置项
    val conf = new FlinkJedisPoolConfig.Builder()
      //Redis主机名
      .setHost("localhost")
      //Redis端口号
      .setPort(6379)
      .build()
    //添加Sink
    dataStream.addSink( new RedisSink[(String,Long,Double)](conf, new MyRedisMapper) )
    //开始执行
    env.execute()
  }

  /**
   * 定义Redis到Mapper
   */
  class MyRedisMapper extends RedisMapper[(String,Long,Double)]{
    /**
     * 获取命令描述
     * @return
     */
    override def getCommandDescription: RedisCommandDescription = new RedisCommandDescription(RedisCommand.SET, "sensor")

    /**
     * 从数据中获取Key
     * @param in 传感器数据的三元组
     * @return Redis的Key
     */
    override def getKeyFromData(in: (String,Long,Double)): String = in._1

    /**
     * 从数据中获取Value
     * @param in 传感器数据的三元组
     * @return Redis的Value
     */
    override def getValueFromData(in: (String,Long,Double)): String = in._3.toString
  }
}
