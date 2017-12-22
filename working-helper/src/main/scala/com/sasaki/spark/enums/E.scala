package com.sasaki.spark.enums

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-12-14 上午10:34:15
 * @Description 常量、枚举
 */
object SparkType {
  
  import org.apache.spark.sql.{ SparkSession, DataFrame, Dataset}
  
  type Spark          = SparkSession
  type SC             = org.apache.spark.SparkContext
  type Streaming      = org.apache.spark.streaming.StreamingContext
  type RDD[T]         = org.apache.spark.rdd.RDD[T]
  type DF             = DataFrame
  type DS[T]          = Dataset[T]
  type Row            = org.apache.spark.sql.Row
}

object LaunchMode extends Enumeration {
  type LaunchMode = Value
  val DEVELOP = Value("DEVELOP")
  val DEPLOY = Value("DEPLOY")
}

object Master {
  val local_1 = "local[1]"
  val yarn = "yarn"
}