package com.sasaki.spark

import com.sasaki.o.Util


/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-29 上午11:39:28
 * @Description 公共特质类
 */
trait T {
  
  def _conf_(name: String, settings: List[(String, String)], master: String = "local[1]") = 
    new org.apache.spark.SparkConf().setAppName(name).setMaster(master).setAll(settings)
    
  def _spark_(conf: org.apache.spark.SparkConf) = org.apache.spark.sql.SparkSession.builder().config(conf).getOrCreate()
  
  def initHandler(f_x: () => org.apache.spark.SparkConf) = ???
  
  /**
   * 无参数启用Spark Handler
   */
  def invokeHandler(sc: org.apache.spark.SparkContext)(f_x: () => Unit) = try f_x finally sc.stop
  
  def invokeHandler(ssc: org.apache.spark.streaming.StreamingContext)(f_x: () => Unit) = try { f_x(); ssc.start(); ssc.awaitTermination() } finally ssc.stop()
}

