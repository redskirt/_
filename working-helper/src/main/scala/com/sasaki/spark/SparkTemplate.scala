package com.sasaki.spark

import com.sasaki.spark.enums.SparkType._
import com.sasaki.spark.enums.Master

object SparkTemplate extends SparkHandler {
  import logger._

  def main(args: Array[String]): Unit = {
    val settings = Map("_key_" -> "_value_")
    val conf = buildConf(independent.getSimpleName(this), settings, Master.LOCAL_*)
    val spark: Spark = buildSparkSession(conf, true)

    invokeSparkHandler(spark) { () =>
      val arr = Seq(1, 2, 4, 5, 6, 8, 4, 5, 2, 334)
      spark.sparkContext.parallelize(arr) foreach println
      
    }
  }
} 