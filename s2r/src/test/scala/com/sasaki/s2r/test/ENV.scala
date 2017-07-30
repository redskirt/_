package com.sasaki.s2r.test

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


