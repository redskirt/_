package com.sasaki.spark

import org.apache.spark.{ SparkConf }
import org.apache.spark.sql.SparkSession

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-29 上午11:39:28
 * @Description For Spark Call.
 */
trait SparkHandler {
  import SparkHandler._
  import com.sasaki.spark.enums._
  import com.sasaki.spark.enums.SparkType._

  def buildConf(appName: String, settings: Seq[(String, String)] = defaultSettings, master: String = Master.local_1) = 
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
  def initHandler(appName: String, settings: Seq[(String, String)], master: String = Master.local_1, enableHive: Boolean = false) = 
    buildSparkSession(buildConf(appName, settings, master))
    
  /**
   * by Custom
   */
  def initHandler(conf: SparkConf, enableHive: Boolean) = 
    if(enableHive) buildSparkSession(conf, enableHive) else buildSparkSession(conf)

  def invokeSparkHandler[S <: { def stop(): Unit }](spark_* : S)(f_x: () => Unit) = try f_x() finally spark_*.stop
     
  @deprecated
  def invokeSessionHandler(f_x: () => Unit)(implicit spark: Spark) = try f_x() finally spark.stop
  @deprecated
  def invokeContextHandler(f_x: () => Unit)(implicit sc: SC) = try f_x() finally sc.stop
  @deprecated
  def invokeStreamingHandler(f_x: () => Unit)(implicit ssc: Streaming) = try { f_x(); ssc.start(); ssc.awaitTermination() } finally ssc.stop()
  
}

object SparkHandler {
   val defaultSettings = Seq(
    ("spark.serializer", "org.apache.spark.serializer.KryoSerializer"))
}

class A(name: String, ll: List[String], a: Int) {
  val aaa : String = ""
}
case class OP(name: String, ll: List[String])

object Main {
  import reflect._
  import scala.reflect.runtime.universe._
    
    def main(args: Array[String]): Unit = {
    
    println {
      typeOf[OP].toString() 
    }
      
    } 
}
