package com.sasaki.spark

import independent._
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.sql.SparkSession

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-29 上午11:39:28
 * @Description 公共特质类
 */
trait T {

  def _conf_(name: String, settings: List[(String, String)], master: String = "local[1]") =
    new SparkConf().setAppName(name).setMaster(master).setAll(settings)

  def _spark_(conf: SparkConf) = SparkSession.builder().config(conf).getOrCreate()

  def initHandler(f_x: () => SparkConf) = ???

  def invokeHandler(spark: SparkSession)(f_x: () => Unit) = try f_x() finally spark.stop
  def invokeHandler(sc: SparkContext)(f_x: () => Unit) = try f_x() finally sc.stop
  def invokeHandler(ssc: StreamingContext)(f_x: () => Unit) = try { f_x(); ssc.start(); ssc.awaitTermination() } finally ssc.stop()

  def invokeHandler_(spark: SparkSession)(f_x: () => Unit) = try f_x() finally spark.stop

}

