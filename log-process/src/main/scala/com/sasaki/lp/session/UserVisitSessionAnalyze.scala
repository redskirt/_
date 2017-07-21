package com.sasaki.lp.session

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

import com.sasaki.lp.persistence.LppSchema._
import com.sasaki.lp.persistence.QueryHelper._
import com.sasaki.lp.poso._
import com.sasaki.lp.enums.E._
import com.sasaki.lp.util.Util

class UserVisitSessionAnalyze {
  
}



object UserVisitSessionAnalyze {
  val conf = new SparkConf()
    .setAppName("UserVisitSessionAnalyze")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  
  /**
   * 查询用户提交时间范围的访问记录作为公共RDD
   */
  def rddUserActionFunc(sc: SparkContext, sqlContext: SQLContext, startDate: String, finishDate : String) = {
    val strUserVisitAction = "date$user_id$session_id$page_id$action_time$search_keyword$click_category_id$click_product_id$order_category_ids$order_product_ids$pay_category_ids$pay_product_ids$city_id$"
	  
	  val schemaUserVisitAction = StructType({
      val o = strUserVisitAction.split($)
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

    val rowUserVisitAction = sc.textFile("E:\\mock\\user_visit_action"/*"hdfs://node01:8020/tmp/user_visit_action"*/, 4)
      .map(__ => {
        val o = __.split('|')
        Row(o(0), o(1).toLong, o(2), o(3).toLong, o(4), o(5), o(6).toLong, o(7).toLong, o(8), o(9), o(10), o(11), o(12).toLong)
      })
      
    val dfUserVisitAction = sqlContext.createDataFrame(rowUserVisitAction, schemaUserVisitAction)
    dfUserVisitAction.createOrReplaceTempView("user_visit_action")
    //    sqlContext.sql("select count(0) from user_visit_action").show()  
    
    val sqlCount = s"""select count(0) from user_visit_action where date>='$startDate' and date<='$finishDate'"""
    sqlContext.sql(sqlCount).show()
    assert(0 != sqlContext.sql(sqlCount).collect()(0).get(0).toString.toInt, "--> Have no dataset from this query.")    
    
    // 公共RDD
    val rddUserAction = sqlContext.sql(s"""select * from user_visit_action where date>='$startDate' and date<='$finishDate'""").rdd.repartition(10)
    
    rddUserAction
  }
  
  /**
   * rddSessionID___Action 按session_id进行分组聚合；
   * Action中，仅得到商品的搜索词和种类信息；
   * 将rddSessionID___Action 与 user_info 按user_id关联。
   */
  def rddSessionId___UserAction_UserInfoFunc(sqlContext: SQLContext, rddSessionId___UserAction: RDD[(String, Row)]): RDD[(String, String)] = {
    // 将RDD[SessionId, UserAction_]重新映射，构造 RDD[UserId -> sessionid,searchKeywords,clickCategoryIds]
    val rddUserId___UserAction = rddSessionId___UserAction
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
          // 拼接格式 k1->v1$k2->v2  
          $FIELD_SESSION_ID + -> + session_id + $ +
          $FIELD_SEARCH_KEYWORDS + -> + searchKeywords + $ +
          $FIELD_CLICK_CATEGORY_IDS + -> + clickCategoryIds 
        )
      })
    
    // 查询用户信息RDD[UserId, UserInfo]
    val rddUserId___UserInfo = sqlContext.sql("select * from user_info")
      .rdd 
      .map(__ => (__.getLong(0), __))
      
   val rddSessionId___userAction_userInfo = rddUserId___UserAction
     .join(rddUserId___UserInfo) // 将session粒度聚合数据，与用户信息进行join
     .map {__ =>
       val userAction: String = __._2._1
       val userInfo: Row = __._2._2
       val sessionId = Util.keyFrom($FIELD_SESSION_ID, userAction)

       (sessionId, 
           userAction + $ +
           $FIELD_AGE + ->  + userInfo.getInt($age) + $ +
           $FIELD_PROFESSIONAL + -> + userInfo.getString($professional) + $ +
           $FIELD_CITY + -> + userInfo.getString($city) + $ +
           $FIELD_GENDER + -> + userInfo.getString($gender)
       )
    }
    rddSessionId___userAction_userInfo
  }
  
  def main(args: Array[String]): Unit = {
    conf.setMaster("local[*]")
    
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    
	  val strUserInfo        = "user_id$username$name$age$professional$city$gender"
	  val strProductInfo     = "product_id$product_name$extend_info"

	  val schemaUserInfo = StructType({
      val o = strUserInfo.split($)
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
    	val o = strProductInfo.split($)
    	List(
    	  StructField(o(0), LongType, true),
        StructField(o(1), StringType, true),
        StructField(o(2), StringType, true)   
    	)
    })

    val task: Task = queryById(1, $task)
    val params = task.taskParam
    
    // 映射RDD sessionId___userAction，并分组
    implicit val formats = DefaultFormats
//    implicit def anyToT[T <: Any](a: Any): T = a.asInstanceOf[T]
//    val startDate: String = Util.extractFrom($PARAM_START_DATE, params)(Predef.String)
    
    val rddUserAction = rddUserActionFunc(sc, sqlContext, "???" , "???" /*startDate, Util.extractFrom($PARAM_FINISH_DATE, params)*/)
    // 持久化公用RDD rddUserAction
    // val rddUserAction_ = rddUserAction.persist(StorageLevel.MEMORY_ONLY/**纯内存方式等同于cache*/)
    val rddUserAction_ = rddUserAction.cache
    
    val rddSessionId___UserAction: RDD[(String, Row)] = rddUserAction.mapPartitions(__ => {
      var list = List[(String, Row)]()
      while (__.hasNext) {
        val o = __.next
        list = (o.getString(2)/*session_id*/, o) :: list
      }
      list.toIterator
    }, true)
    
    val rddSessionId___UserAction_UserInfo: RDD[(String, String)] = rddSessionId___UserAction_UserInfoFunc(sqlContext, rddSessionId___UserAction)
   
    // 根据params 过滤 rddSessionId___UserAction_UserInfo
    
    val _fromAge_ = Util.extractFrom($PARAM_FROM_AGE, params)
    val _toAge_ =  Util.extractFrom($PARAM_TO_AGE, params)
    val _professionals_ =  Util.extractFrom($PARAM_PROFESSIONALS, params)
    val _cities_ =  Util.extractFrom($PARAM_CITIES, params)
    val _gender_ =  Util.extractFrom($PARAM_GENDER, params)
    val _keywords_ =  Util.extractFrom($PARAM_KEYWORDS, params)
    val _categoryIds_ =  Util.extractFrom($PARAM_CATEGORY_IDS, params)
    
    val _param_ = 
      $PARAM_FROM_AGE + -> + _fromAge_ + $ +
      $PARAM_TO_AGE + -> + _toAge_ + $ +
      $PARAM_PROFESSIONALS + -> + _professionals_ + $ +
      $PARAM_CITIES + -> + _cities_ + $ +
      $PARAM_GENDER + -> + _gender_ + $ +
      $PARAM_KEYWORDS + -> + _keywords_ + $ +
      $PARAM_CATEGORY_IDS + -> + _categoryIds_
      
    val rddFilteredSessionId___UserAction_UserInfo = rddSessionId___UserAction_UserInfo.filter{__ => 
      val userAction_UserInfo = __._2
      
      // ------- TODO
      false
    }  
      
    val rddFilteredSessionId___UserAction_UserInfo_ = rddFilteredSessionId___UserAction_UserInfo.cache()
      
//    val p = rddSessionId___UserAction_UserInfo.join(rddSessionId___UserAction)  
    
    sc.stop()
  }
}

