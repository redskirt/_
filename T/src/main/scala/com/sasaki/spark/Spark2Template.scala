package com.sasaki.spark

import org.apache.spark.sql._
import org.apache.spark._

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08*28 下午2:59:24
 * @Description
 */
class Spark2Template(settings: List[(String, String)]) extends AnyRef with T {
  val conf = _conf_(independent.getSimpleName(this), settings, "local[1]")
  
  val spark: SparkSession = _spark_(conf)
  val dataSet: Dataset[String] = spark.read.textFile("")
  val dataFrame: DataFrame = spark.readStream.json("")
    
  invokeHandler(spark) { () => }
  
}

object Spark2Template {
  val settings = List(("_key_" -> "_value_"))
 
  def main(args: Array[String]): Unit = {

    
  }
}
