package com.redislabs.provider.redis.rdd

import com.redislabs.provider.redis.RedisConfig
import org.apache.spark.SparkContext
import com.redislabs.provider.redis.RedisContext
import com.redislabs.provider.redis.RedisStreamingContext
import org.apache.spark.streaming.StreamingContext

trait ENV {
  var sc: SparkContext = _
  var redisConfig: RedisConfig = _
  var content: String = _
}

trait RedisFunctions {
  implicit def toRedisContext(sc: SparkContext): RedisContext = new RedisContext(sc)
  implicit def toRedisStreamingContext(ssc: StreamingContext): RedisStreamingContext = new RedisStreamingContext(ssc)
}