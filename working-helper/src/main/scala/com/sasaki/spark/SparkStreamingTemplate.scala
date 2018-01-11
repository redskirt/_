package com.sasaki.spark

import com.sasaki.spark.enums.SparkType._
import org.apache.spark.streaming.Seconds

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-29 下午2:07:42
 * @Description
 */
class SparkStreamingTemplate(val duration: Duration) {

}

object SparkStreamingTemplate extends AnyRef with SparkHandler {
  
  val conf = buildConf(independent.getSimpleName(this), Map("_key_" -> "_value_"))

  val ssc = new Streaming(conf, new SparkStreamingTemplate(Seconds(10)).duration)
  
}
