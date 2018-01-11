package com.sasaki.spark.template

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2018-01-11 18:10:03
 * @Description Spark SQL, DataFrames, Datasets 相互转换示例
 */
object SparkDataModelTransformingCase extends com.sasaki.spark.SparkHandler {

  implicit val spark = buildLocalSparkSession(true)

  import logger._
  import spark.implicits._

  def main(args: Array[String]): Unit = {

    invokeSessionHandler { () =>
      val dfJson = spark.read.json("src/main/resources/sample/people.json")
      dfJson.show()

    }
  }
}