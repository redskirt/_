package com.sasaki.spark.template

import com.sasaki.spark.enums.SparkType._
import com.sasaki.spark.SparkHandler

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2018-01-08 下午5:07:47
 * @Description 产生Spark实例，仅本地任务使用
 */
object PrimitiveSparkTemplate extends SparkHandler {
  
  import logger._
  
  lazy val spark: Spark = buildLocalSparkSession(false)
  
  def main(args: Array[String]): Unit = {
    info("> --------------------------------- Spark will start by local model. ---------------------------------------------")

    /**
     * Option 1
     */
    implicit val _spark_ = spark
    invokeSessionHandler { () =>
      spark.sparkContext.parallelize(Seq(1 to 10)) foreach println
    }
    
    /**
     * Option 2
     */
    invokeSparkHandler(spark) { () =>
      spark.sparkContext.parallelize(Seq(1 to 10)) foreach println
    }
  }
} 