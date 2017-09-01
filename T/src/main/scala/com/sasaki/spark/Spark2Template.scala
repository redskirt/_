package com.sasaki.spark

import org.apache.spark.SparkConf

import com.sasaki.o.Util
import org.apache.spark.sql._

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08*28 下午2:59:24
 * @Description
 */
class Spark2Template {

}

object Spark2Template extends Object with T {

  val settings = List(
    ("_key_" -> "_value_"))
  val conf = _conf_(Util.getSimpleName(this), settings, "local[1]")

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = _spark_(conf)

    val dataSet: Dataset[String] = spark.read.textFile("")
    val dataFrame: DataFrame = spark.readStream.json("")

  }
}
