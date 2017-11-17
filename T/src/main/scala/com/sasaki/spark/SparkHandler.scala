package com.sasaki.spark

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.sql.SparkSession

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-29 上午11:39:28
 * @Description For Spark Call.
 */
trait SparkHandler {
  import SparkHandler._

  def buildConf(name: String, settings: Seq[(String, String)] = defaultSettings, master: String = Master.local_1) = new SparkConf().setAppName(name).setMaster(master).setAll(settings)
    
  def buildSparkSession(conf: SparkConf, enableHive: Boolean = false) = {
    val builder = SparkSession.builder().config(conf)
    if(enableHive) builder.enableHiveSupport()
    builder.getOrCreate()
  }
  
  def buildSparkContext(conf: SparkConf) = buildSparkSession(conf: SparkConf).sparkContext
  
  /**
   * by Default Local
   */
  def initHandler(name: String, settings: List[(String, String)], master: String = Master.local_1) = 
    buildSparkSession(buildConf(name, settings, master))
    
  /**
   * by Custom
   */
  def initHandler(conf: SparkConf, enableHive: Boolean) = 
    if(enableHive) buildSparkSession(conf, enableHive) else buildSparkSession(conf)

  def invokeSessionHandler(f_x: () => Unit)(implicit spark: SparkSession) = try f_x() finally spark.stop
  def invokeContextHandler(f_x: () => Unit)(implicit sc: SparkContext) = try f_x() finally sc.stop
  def invokeStreamingHandler(f_x: () => Unit)(implicit ssc: StreamingContext) = try { f_x(); ssc.start(); ssc.awaitTermination() } finally ssc.stop()

}

object SparkHandler {
   val defaultSettings = Seq(
    ("spark.serializer", "org.apache.spark.serializer.KryoSerializer"))
    
   object Master {
    val local_1 = "local[1]"
    val yarn = "yarn"
  }
  
  object LaunchMode extends Enumeration {
    type LaunchMode = Value
    val DEVELOP = Value("DEVELOP")
    val DEPLOY = Value("DEPLOY")
  }
}

