package com.sasaki.spark

import org.apache.spark.sql.SparkSession

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-28 下午2:59:24
 * @Description
 */
class Spark2Template {
  
}

object Spark2Template extends SparkHandler {
  val settings = List(("_key_" -> "_value_"))
  val conf = _conf_(independent.getSimpleName(this), settings, "local[1]")
  
  val spark: SparkSession = _sparkSession_(conf, true)
//  val dataSet: Dataset[String] = spark.read.textFile("")
//  val dataFrame: DataFrame = spark.readStream.json("")
 
  def main(args: Array[String]): Unit = {
      System.setProperty("hadoop.home.dir", "H:\\hadoop-common-2.2.0-bin-master" /*调试启用临时目录*/ )
  }
}
