package com.sasaki.spark

import com.sasaki.o.Util
import scala.reflect.ClassTag


/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-29 上午11:39:28
 * @Description 公共特质类
 */
trait T {
  val conf = new org.apache.spark.SparkConf()
  
  def initHandler(f: () => org.apache.spark.SparkConf) = ???
  
  /**
   * 无参数启用Spark Handler
   */
  def invokeHandler(sc: org.apache.spark.SparkContext)(f: () => Unit) = try f() finally sc.stop
  
  def invokeHandler(ssc: org.apache.spark.streaming.StreamingContext)(f: () => Unit) = try { f(); ssc.start(); ssc.awaitTermination() } finally ssc.stop()
}

