package com.sasaki.spark

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-29 上午11:39:28
 * @Description 提供Spark初始化、模板调用
 */
trait SparkHandler extends LazyLogging {
  import com.sasaki.spark.enums._
  import com.sasaki.spark.enums.SparkType._
  import Master._

  private val defaultSettings = Map(
    ("spark.serializer"           -> "org.apache.spark.serializer.KryoSerializer"),
    ("spark.executor.memory"      -> "2G"),
    ("spark.driver.memory"        -> "1G"),
    ("spark.driver.cores"         -> "2"),
    ("spark.driver.maxResultSize" -> "10G"),
    ("spark.total.executor.cores" -> "2")
  )

  def buildConf(
    appName: String,
    settings: Map[String, String] = defaultSettings,
    master: Master = Master.LOCAL_1) =
    new SparkConf().setAppName(appName).setMaster(Master.$(master)).setAll(settings)
    
  def buildSparkSession(conf: SparkConf, enableHive: Boolean = false) = {
    val builder = SparkSession.builder().config(conf)
    if(enableHive) builder.enableHiveSupport()
    builder.getOrCreate()
  }
  
  def buildSparkContext(conf: SparkConf) = buildSparkSession(conf: SparkConf).sparkContext
  
  /**
   * 快速构造Spark，仅用于本地调试，生产项目慎用！
   */
  def buildLocalSparkSession(enableHive: Boolean = false) = {
    System.setProperty("hadoop.home.dir", "H:\\hadoop-common-2.2.0-bin-master" /*调试启用临时目录*/ )
    buildSparkSession(buildConf("spark-local", defaultSettings, Master.LOCAL_*), enableHive)
  }
      
  def initStreamingHandler = ???

  def invokeSparkHandler[T <: { def stop(): Unit }](spark_* : T)(f_x: () => Unit) = 
    try f_x() finally spark_*.stop
     
  def invokeSessionHandler(f_x: () => Unit)(implicit spark: Spark) = 
    try f_x() finally spark.stop
    
  @deprecated("For Spark-1.* version.")
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
