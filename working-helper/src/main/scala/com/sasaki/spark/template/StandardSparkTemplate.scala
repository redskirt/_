package com.sasaki.spark.template

import com.sasaki.spark.enums.LaunchMode
import com.sasaki.spark.enums.SparkType._
import com.sasaki.spark.SparkHandler

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2018-01-08 下午5:18:23
 * @Description
 */
object StandardSparkTemplate extends SparkHandler {

  type M = LaunchMode.Value

  val conf = buildConfWithoutMaster(DEFAULT_SETTINGS)

  lazy val spark = buildAutomaticOnYarnSparkSession(conf, _mode_, false)
  lazy val sc = spark.sparkContext

  import logger._
  import spark._
  import spark.implicits._

  implicit var _mode_ : M = _
  
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

    /**
     * _spark_ 必须在 _mode_ 之后初始化，避免提前触发 lazy spark 初始化
     */
//    implicit val _spark_ = spark
//    invokeSessionHandler { () => ??? }
    
    invokeSparkHandler(spark) { () => 
      // 测试样例仅本地通过
      spark.read.textFile(s"${reflect.classpath}deploy").rdd
        .flatMap(_.split(independent.$s)).map((_, 1)).reduceByKey(_ + _) foreach println
    }
  }
}