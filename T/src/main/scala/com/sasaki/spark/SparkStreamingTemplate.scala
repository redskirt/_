package com.sasaki.spark

import org.apache.spark.streaming.StreamingContext

import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.Duration


/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-29 下午2:07:42
 * @Description
 */
class SparkStreamingTemplate(val duration: Duration) {

}

object SparkStreamingTemplate extends AnyRef with SparkHandler {
  
  val conf = _conf_(independent.getSimpleName(this), List(("_key_" -> "_value_")))

  val ssc = new StreamingContext(conf, new SparkStreamingTemplate(Seconds(10)).duration)
  
}
