package com.sasaki.spark

import com.sasaki.spark.enums.SparkType._
import com.sasaki.spark.enums.Master

object SparkTemplate extends SparkHandler {

  def main(args: Array[String]): Unit = {
    val settings = Map("_key_" -> "_value_")
    val conf = buildConf(independent.getSimpleName(this), settings, Master.LOCAL_*)
    val spark: Spark = buildSparkSession(conf, true)

    System.setProperty("hadoop.home.dir", "H:\\hadoop-common-2.2.0-bin-master" /*调试启用临时目录*/ )
    
    invokeSparkHandler(spark) { () =>
      val arr = Seq(1, 2, 4, 5, 6, 8)
      spark.sparkContext.parallelize(arr) foreach println
      
    }
  }
} 