package com.sasaki.spark

import com.sasaki.spark.enums.LaunchMode
import com.sasaki.spark.enums.SparkType._

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2018-01-08 下午5:18:23
 * @Description
 */
object StandardSparkTemplate extends SparkHandler {
  import logger._

  type M = LaunchMode.Value

  val conf = buildConfWithoutMaster(DEFAULT_SETTINGS)

  lazy val spark = buildAutomaticOnYarnSparkSession(conf, _mode_, false)
  lazy val sc = spark.sparkContext

  import spark._
  import spark.implicits._

  implicit var _mode_ : M = _
  implicit var _spark_ : Spark = _
  
  def main(args: Array[String]): Unit = {
    args match {
      case Array() =>
        info("> --------------------------------- Spark will start by local model. ---------------------------------------------")
        _mode_ = LaunchMode.DEVELOP
      // TODO Invoking fake data...
      case Array(_1) =>
        info("> --------------------------------- Spark will start by cluster model. ---------------------------------------------")
        _mode_ = LaunchMode.DEPLOY
      case _ =>
        error("Init args exception " + (args.foreach(o => print(o + " "))) + ", expect input: ... ")
        throw new IllegalArgumentException(s"Init args exception $args")
    }

//    invokeSessionHandler { () => ??? }
    invokeSparkHandler(spark) { () => 
      spark.read.textFile(s"${reflect.classpath}deploy").rdd
        .flatMap(_.split(independent.$s)).map((_, 1)).reduceByKey(_ + _) foreach println
    }
  }
}