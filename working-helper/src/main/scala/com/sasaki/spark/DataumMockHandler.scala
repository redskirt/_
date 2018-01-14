package com.sasaki.spark

import com.sasaki.spark.enums.SparkType._
import com.sasaki.packages.constant.original._

/**
 * 
 */
trait DataumMockHandler {
  
  def createMockRDD[T: CT](implicit spark: Spark): RDD[T] = 
		  spark.sparkContext.parallelize(mock)
  
//  def createHiveTempView[T: CT](clazz: Class[_])(f_x: () => Seq[T])(implicit spark: Spark) =
//      spark.createDataFrame(f_x())
  def mock[T: CT]: List[T]
}

