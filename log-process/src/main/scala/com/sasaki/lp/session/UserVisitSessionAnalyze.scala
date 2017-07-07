package com.sasaki.lp.session

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types._
import org.apache.spark.sql.Row
import org.apache.spark.storage.StorageLevel

class UserVisitSessionAnalyze {
  
   
}

object UserVisitSessionAnalyze {
  val conf = new SparkConf()
    .setAppName("UserVisitSessionAnalyze")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  
  def main(args: Array[String]): Unit = {
    conf.setMaster("local[*]")
    
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    
	  val | = '|'

	  val strUserVisitAction = "date|user_id|session_id|page_id|action_time|search_keyword|click_category_id|click_product_id|order_category_ids|order_product_ids|pay_category_ids|pay_product_ids|city_id|"
	  val strUserInfo        = "user_id|username|name|age|professional|city|sex"
	  val strProductInfo     = "product_id|product_name|extend_info"
	  
	  val schemaUserVisitAction = StructType({
      val o = strUserVisitAction.split(|)
      List(
        StructField(o(0), StringType, true),
        StructField(o(1), LongType, true),
        StructField(o(2), StringType, true),
        StructField(o(3), LongType, true),
        StructField(o(4), StringType, true),
        StructField(o(5), StringType, true),
        StructField(o(6), LongType, true),
        StructField(o(7), LongType, true),
        StructField(o(8), StringType, true),
        StructField(o(9), StringType, true),
        StructField(o(10), StringType, true),
        StructField(o(11), StringType, true),
        StructField(o(12), LongType, true)
      )
    })
    
    val schemaUserInfo = StructType({
      val o = strUserInfo.split(|)
      List(
        StructField(o(0), LongType, true),
        StructField(o(1), StringType, true),
        StructField(o(2), StringType, true),
        StructField(o(3), IntegerType, true),
        StructField(o(4), StringType, true),
        StructField(o(5), StringType, true),
        StructField(o(6), StringType, true)
      )
    })
    
    val schemaProductInfo = StructType({
    	val o = strProductInfo.split(|)
    	List(
    	  StructField(o(0), LongType, true),
        StructField(o(1), StringType, true),
        StructField(o(2), StringType, true)   
    	)
    })
	  
    val rowUserVisitAction = sc.textFile("E:\\mock\\user_visit_action"/*"hdfs://node01:8020/tmp/user_visit_action"*/, 4)
      .map(__ => {
        val o = __.split(|)
        Row(o(0), o(1).toLong, o(2), o(3).toLong, o(4), o(5), o(6).toLong, o(7).toLong, o(8), o(9), o(10), o(11), o(12).toLong)
      })
    
    val dfUserVisitAction = sqlContext.createDataFrame(rowUserVisitAction, schemaUserVisitAction)
    dfUserVisitAction.createOrReplaceTempView("user_visit_action")
    //    sqlContext.sql("select count(0) from user_visit_action").show()
    
    val date = "2017-07-05"
    val date_ = "2017-07-05"
    
    val sqlCount = s"""select count(0) from user_visit_action where date>='$date' and date<='$date_'"""
    assert(0 != sqlContext.sql(sqlCount).collect()(0).get(0).toString.toInt, "--> Have no dataset from this query.")    
    
    val sql = s"""select * from user_visit_action where date>='$date' and date<='$date_'"""
    sqlContext.sql(s"""select count(0) from user_visit_action where date>='$date' and date<='$date_'""").show()
    
    // 作为公共RDD
    val rddAction = sqlContext.sql(sql).rdd.repartition(5)
    
    val rddSessionID___Action = rddAction.mapPartitions(__ => {
      val list = List[(String, Row)]()
      
      while (__.hasNext) {
        val o = __.next
        (o.getString(2)/*session_id*/, o) :: list
      }
//      println(list.size)
      
      list.toIterator
    }, true)
    
//    val rddSessionID___Action = rddAction.map(__ => (__.getString(2), __))
    
    // 持久化RDD
    // val rddSessionID___Action_ = rddSessionID___Action.persist(StorageLevel.MEMORY_ONLY/**纯内存方式等同于cache*/)
    // val rddSessionID___Action_ = rddSessionID___Action.cache()
    
    println(rddSessionID___Action.count())
//    rddSessionID___Action.take(2).foreach(__ => println(__ + "__"))
    
    sc.stop()
  }
}