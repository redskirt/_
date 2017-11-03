package com.sasaki.spark

import independent._
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
  protected object Master {
    val local_1 = "local[1]"
    val yarn = "yarn"
  }

  protected object LaunchMode extends Enumeration {
    type AssemblyMode = Value
    val DEVELOP = Value("DEVELOP")
    val DEPLOY = Value("DEPLOY")
  }

  def _conf_(name: String, settings: List[(String, String)], master: String = null) = {
    val conf = new SparkConf().setAppName(name).setAll(settings)
    if(nonNull(master)) conf.setMaster(master)
    conf
  }
    
  def _sparkSession_(conf: SparkConf, enableHive: Boolean = false) = {
    val builder = SparkSession.builder().config(conf)
    if(enableHive) builder.enableHiveSupport()
    builder.getOrCreate()
  }
  
  def _sparkContext_(conf: SparkConf) = _sparkSession_(conf: SparkConf).sparkContext

  /**
   * by Default Local
   */
  def initHandler(name: String, settings: List[(String, String)], master: String = Master.local_1) = 
    _sparkSession_(_conf_(name, settings, master))
    
  /**
   * by Custom
   */
  def initHandler(conf: SparkConf, enableHive: Boolean) = 
    if(enableHive) _sparkSession_(conf, enableHive) else _sparkSession_(conf)

  def invokeSessionHandler(f_x: () => Unit)(implicit spark: SparkSession) = try f_x() finally spark.stop
  def invokeContextHandler(f_x: () => Unit)(implicit sc: SparkContext) = try f_x() finally sc.stop
  def invokeStreamingHandler(f_x: () => Unit)(implicit ssc: StreamingContext) = try { f_x(); ssc.start(); ssc.awaitTermination() } finally ssc.stop()

}

object Test {
  def main(args: Array[String]): Unit = {
    
  }
}


