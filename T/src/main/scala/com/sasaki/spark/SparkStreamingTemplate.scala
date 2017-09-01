package com.sasaki.spark

import com.sasaki.o.Util
import org.apache.spark.streaming.StreamingContext

import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.Seconds


/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-29 下午2:07:42
 * @Description
 */
class SparkStreamingTemplate {

}

object SparkStreamingTemplate extends Object with T {
  
  val conf = _conf_(Util.getSimpleName(this), List(("_key_" -> "_value_")))

  val ssc = new StreamingContext(conf, Seconds(2))
  
}
