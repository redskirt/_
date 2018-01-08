package com.sasaki.spark

import com.sasaki.spark.enums.SparkType._
import com.sasaki.spark.enums.Master

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2018-01-08 下午5:07:47
 * @Description 产生Spark实例，仅本地任务使用
 */
object PrimitiveSparkTemplate extends SparkHandler {
  import logger._
  

  def main(args: Array[String]): Unit = {
    val spark: Spark = buildLocalSparkSession(true)
    info("> ---------------------------- 初始化Spark完成：{}", spark)
    
    invokeSparkHandler(spark) { () =>
      val arr = Seq(1 to 10)
      spark.sparkContext.parallelize(arr) foreach println
    }
  }
} 