package com.sasaki.o

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-20 上午11:03:52
 * @Description
 */
object SparkTest {
  val conf = new SparkConf()
    .setAppName("SparkTest")
    .setMaster("local[1]")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  val sc = new SparkContext(conf)
  val spark: SparkSession = SparkSession.builder
    .config(conf)
    .getOrCreate

  import spark.implicits._
  //  val sqlContext = new SQLContext(sc);
  //  import sqlContext.implicits._
  case class Person(name: String, age: Int)

  def main(args: Array[String]): Unit = {

    val rddPerson: RDD[Person] = sc.parallelize(Seq(Person("John", 27)))
    val people = rddPerson.toDS()
    val people_ = Seq(Person("John", 27)).toDS()

    sc.stop()
  }

}