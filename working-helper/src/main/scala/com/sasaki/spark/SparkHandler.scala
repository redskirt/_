package com.sasaki.spark

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-29 上午11:39:28
 * @Description 提供Spark初始化、模板调用
 */
trait SparkHandler {
  import com.sasaki.spark.enums._
  import com.sasaki.spark.enums.SparkType._
  
  val defaultSettings = Map(
    ("spark.serializer" -> "org.apache.spark.serializer.KryoSerializer"))
    
  import Master._
  
  def buildConf(
      appName:   String, 
      settings:  Map[String, String] = defaultSettings, 
      master:    String = Master.$(LOCAL_1)) = 
    new SparkConf().setAppName(appName).setMaster(master).setAll(settings)
    
  def buildSparkSession(conf: SparkConf, enableHive: Boolean = false) = {
    val builder = SparkSession.builder().config(conf)
    if(enableHive) builder.enableHiveSupport()
    builder.getOrCreate()
  }
  
  def buildSparkContext(conf: SparkConf) = buildSparkSession(conf: SparkConf).sparkContext
  
  /**
   * by Default Local
   */
  def initHandler(
      appName:   String, 
      settings:  Map[String, String], 
      master:    String = Master.$(LOCAL_1), 
      enableHive: Boolean = false) = 
    buildSparkSession(buildConf(appName, settings, master))
    
  /**
   * by Custom
   */
  def initHandler(conf: SparkConf, enableHive: Boolean) = 
    if(enableHive) 
      buildSparkSession(conf, enableHive) 
    else 
      buildSparkSession(conf)
      
  def initStreamingHandler = ???

  def invokeSparkHandler[T <: { def stop(): Unit }](spark_* : T)(f_x: () => Unit) = 
    try f_x() finally spark_*.stop
     
  def invokeSessionHandler(f_x: () => Unit)(implicit spark: Spark) = 
    try f_x() finally spark.stop
    
  def invokeContextHandler(f_x: () => Unit)(implicit sc: SC) = 
    try f_x() finally sc.stop
    
  def invokeStreamingHandler(f_x: () => Unit)(implicit ssc: Streaming) =
    try {
      f_x()
      ssc.start()
      ssc.awaitTermination()
    } //
    finally ssc.stop()
  
}

