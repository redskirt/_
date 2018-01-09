package com.sasaki.spark

import com.typesafe.scalalogging.slf4j.LazyLogging
import com.sasaki.kit.ReflectHandler

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-29 上午11:39:28
 * @Description 提供Spark初始化、模板调用
 */
trait SparkHandler extends ReflectHandler with LazyLogging {
  import independent._
  import com.sasaki.spark.enums._
  import com.sasaki.spark.enums.SparkType._
  import Master._

  private type Conf = org.apache.spark.SparkConf
  private val SPARK_MASTER = "spark.master"
  
  protected val DEFAULT_SETTINGS = Map(
    ("spark.serializer"           -> "org.apache.spark.serializer.KryoSerializer"),
    ("spark.executor.memory"      -> "2G"),
    ("spark.driver.memory"        -> "1G"),
    ("spark.driver.cores"         -> "2"),
    ("spark.driver.maxResultSize" -> "10G"),
    ("spark.total.executor.cores" -> "2")
  )
  import scala.reflect.runtime.universe._

  /**
   * 兼容 LaunchMode DEVELOP/DEPLOY 同时存在时构造SparkConf。
   * 特别注意：使用该方法后必须手动设置Master
   */
  def buildConfWithoutMaster(
    appName: String,
    settings: Map[String, String]): Conf =
    new Conf()
      .setAppName(appName)
      .setAll(settings)

  /**
   * 同上。
   */
  def buildConfWithoutMaster(settings: Map[String, String]): Conf =
    buildConfWithoutMaster(s"spark-job_$getSuccessorName", settings)
  
  /**
   * 仅 LaunchMode DEVELOP 时构造SparkConf，生产项目慎用！
   */
  def buildConfWithLocal(
    appName: String,
    settings: Map[String, String] = DEFAULT_SETTINGS) =
      buildConfWithoutMaster(appName, settings)
        .setMaster(Master.$(Master.LOCAL_1))

  /**
   * 兼容 LaunchMode DEVELOP/DEPLOY 同时存在时构造SparkSession。
   * 特别注意：该方法强制检查Master，不通过则异常
   */
  def buildSparkSession(conf: Conf, enableHive: Boolean = false) =
    invokeNonEmpty(conf.get(SPARK_MASTER, $e)) { () =>
      val builder = org.apache.spark.sql.SparkSession.builder().config(conf)
      if (enableHive) builder.enableHiveSupport()
      builder.getOrCreate()
    }
  
  @deprecated("兼容 Spark-1.* 版本。")
  def buildSparkContext(conf: Conf) = new org.apache.spark.SparkContext(conf)
  
  /**
   * 快速构造Spark，仅用于本地调试，生产项目慎用！
   */
  def buildLocalSparkSession(enableHive: Boolean = false) = {
    // 调试启用临时目录
    System.setProperty("hadoop.home.dir", "H:\\hadoop-common-2.2.0-bin-master")
    buildSparkSession(buildConfWithLocal("spark-local", DEFAULT_SETTINGS), enableHive)
  }
      
  def initStreamingHandler = ???

  def invokeSparkHandler[T <: { def stop(): Unit }](spark_* : T)(f_x: () => Unit) = 
    try f_x() finally spark_*.stop
     
  def invokeSessionHandler(f_x: () => Unit)(implicit spark: Spark) = 
    try f_x() finally spark.stop
    
  @deprecated("兼容 Spark-1.* 版本。")
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
