package com.sasaki.spark.enums

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-12-14 上午10:34:15
 * @Description 常量、枚举
 */
object SparkType {
  
  import org.apache.spark._
  import sql.{ SparkSession, DataFrame, Dataset}
  
  type Conf           = SparkConf
  type Spark          = SparkSession
  type SC             = SparkContext
  type RDD[T]         = rdd.RDD[T]
  type DF             = DataFrame
  type DS[T]          = Dataset[T]
  type Row            = sql.Row
  
  type Streaming      = streaming.StreamingContext
  type Duration       = streaming.Duration
}

object LaunchMode extends Enumeration {
  
  type LaunchMode = Value
  
  val DEVELOP = Value("DEVELOP")
  val DEPLOY = Value("DEPLOY")
  
  def $(that: LaunchMode.Value) = that.toString()
  
  def isDevelop(mode: LaunchMode.Value) = mode == DEVELOP
  
  def isDeploy(mode: LaunchMode.Value) = mode == DEPLOY
}

object Master extends Enumeration {
  
  type Master = Value
  
  val LOCAL_1 = Value("local[1]")
  val LOCAL_* = Value("local[*]")
  val YARN    = Value("yarn")
  // val MESOS    = Value("mesos")

}
