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
  
  import com.sasaki.lp.enums.E._
  def main(args: Array[String]): Unit = {
    conf.setMaster("local[*]")
    
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    


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
    sqlContext.sql(sqlCount).show()
    assert(0 != sqlContext.sql(sqlCount).collect()(0).get(0).toString.toInt, "--> Have no dataset from this query.")    
    
    // 公共RDD
    val rddAction = sqlContext.sql(s"""select * from user_visit_action where date>='$date' and date<='$date_'""").rdd.repartition(10)
   
    val rddSessionId___Action = rddAction.mapPartitions(__ => {
      var list = List[(String, Row)]()
      while (__.hasNext) {
        val o = __.next
        list = (o.getString(2)/*session_id*/, o) :: list
      }
      list.toIterator
    }, true)
    
    // 持久化RDD
//     val rddSessionID___Action_ = rddSessionID___Action.persist(StorageLevel.MEMORY_ONLY/**纯内存方式等同于cache*/)
    val rddSessionID___Action_ = rddSessionId___Action.cache()
    
    println(rddSessionId___Action.count())
    
    /**
     * rddSessionID___Action 按session_id进行分组聚合；
     * Action中，仅得到商品的搜索词和种类信息；
     * 将rddSessionID___Action 与 user_info 按user_id关联。
     */
    val rddUserId___UserInfo = sqlContext.sql("select * from user_info").rdd.map(__ => (__.getLong(0)/*user_id*/, __))
    
    val rddUserId___ActionInfo = rddSessionID___Action_
      .groupByKey()// session_id 分组
      .map(__ => {
        val session_id = __._1
        val iterator = __._2.iterator
        
        var user_id = 0L
        var searchKeyword = ""
        var clickCategoryId = ""
        
        while(iterator.hasNext) {
          val row = iterator.next()
          user_id = row.getLong(0)
          val searchKeyword_ = row.getString(4)
          val clickCategoryId_ = row.getString(5)
            
          if(searchKeyword_.nonEmpty && !searchKeyword.contains(searchKeyword_))
            searchKeyword = searchKeyword.concat(searchKeyword_ + /)
            
          if(clickCategoryId_.nonEmpty && !clickCategoryId.contains(clickCategoryId_))
          	clickCategoryId = clickCategoryId.concat(clickCategoryId_ + /)
        }
        
        val searchKeywords = searchKeyword.substring(0, searchKeyword.lastIndexOf(/))
        val clickCategoryIds = clickCategoryId.substring(0, clickCategoryId.lastIndexOf(/))
        
        (user_id, 
          // 拼接格式 k1->v1|key2->v2  
          $FIELD_SESSION_ID + -> + session_id + | +
          $FIELD_SEARCH_KEYWORDS + -> + searchKeywords + | +
          $FIELD_CLICK_CATEGORY_IDS + -> + clickCategoryIds 
        )
      })
    
    val rddUserInfo_UserAction = rddUserId___UserInfo.join(rddUserId___ActionInfo)
    
    sc.stop()
  }
}

