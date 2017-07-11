package com.sasaki.lp.enums

object E extends Enumeration {
  
	val |   = '|'
  val ||  = "|"
  val /   = ","
  val ->  = "->"
	  
	// 配置
	val $JDBC_DRIVER 					        = Value("jdbc.driver")
	val $JDBC_DATASOURCE_SIZE 			  = Value("jdbc.datasource.size")
	val $JDBC_URL 						        = Value("jdbc.url")
	val $JDBC_USER 						        = Value("jdbc.user")
	val $JDBC_PASSWORD 					      = Value("jdbc.password")
	val $JDBC_URL_PROD 					      = Value("jdbc.url.prod")
	val $JDBC_USER_PROD 				      = Value("jdbc.user.prod")
	val $JDBC_PASSWORD_PROD 			    = Value("jdbc.password.prod")
	val $SPARK_LOCAL 					        = Value("spark.local")
	val $SPARK_LOCAL_TASKID_SESSION 	= Value("spark.local.taskid.session")
	val $SPARK_LOCAL_TASKID_PAGE 		  = Value("spark.local.taskid.page")
	val $SPARK_LOCAL_TASKID_PRODUCT 	= Value("spark.local.taskid.product")
	val $KAFKA_METADATA_BROKER_LIST 	= Value("kafka.metadata.broker.list")
	val $KAFKA_TOPICS 					      = Value("kafka.topics")
	
	//Spark作业
	val $SPARK_APP_NAME_SESSION 	= Value("UserVisitSessionAnalyzeSpark")
	val $SPARK_APP_NAME_PAGE 			= Value("PageOneStepConvertRateSpark")
	val $FIELD_SESSION_ID 				= Value("sessionid")
	val $FIELD_SEARCH_KEYWORDS 		= Value("searchKeywords")
	val $FIELD_CLICK_CATEGORY_IDS = Value("clickCategoryIds")
	val $FIELD_AGE 						    = Value("age")
	val $FIELD_PROFESSIONAL 			= Value("professional")
	val $FIELD_CITY 					    = Value("city")
	val $FIELD_SEX 						    = Value("sex")
	val $FIELD_VISIT_LENGTH 			= Value("visitLength")
	val $FIELD_STEP_LENGTH 				= Value("stepLength")
	val $FIELD_START_TIME 				= Value("startTime")
	val $FIELD_CLICK_COUNT 				= Value("clickCount")
	val $FIELD_ORDER_COUNT 				= Value("orderCount")
	val $FIELD_PAY_COUNT 				  = Value("payCount")
	val $FIELD_CATEGORY_ID 				= Value("categoryid")
	val $SESSION_COUNT 					  = Value("session_count")

	val $TIME_PERIOD_1s_3s 				= Value("1s_3s")
	val $TIME_PERIOD_4s_6s 				= Value("4s_6s")
	val $TIME_PERIOD_7s_9s 				= Value("7s_9s")
	val $TIME_PERIOD_10s_30s 			= Value("10s_30s")
	val $TIME_PERIOD_30s_60s 			= Value("30s_60s")
	val $TIME_PERIOD_1m_3m 				= Value("1m_3m")
	val $TIME_PERIOD_3m_10m 			= Value("3m_10m")
	val $TIME_PERIOD_10m_30m 			= Value("10m_30m")
	val $TIME_PERIOD_30m 				  = Value("30m")

	val $STEP_PERIOD_1_3 				  = Value("1_3")
	val $STEP_PERIOD_4_6 				  = Value("4_6")
	val $STEP_PERIOD_7_9 				  = Value("7_9")
	val $STEP_PERIOD_10_30 				= Value("10_30")
	val $STEP_PERIOD_30_60 				= Value("30_60")
	val $STEP_PERIOD_60 				  = Value("60")
	
	// 任务参数
	val $PARAM_START_DATE 				= Value("startDate")
	val $PARAM_END_DATE 				  = Value("endDate")
	val $PARAM_START_AGE 				  = Value("startAge")
	val $PARAM_END_AGE 					  = Value("endAge")
	val $PARAM_PROFESSIONALS 			= Value("professionals")
	val $PARAM_CITIES 					  = Value("cities")
	val $PARAM_SEX 						    = Value("sex")
	val $PARAM_KEYWORDS 				  = Value("keywords")
	val $PARAM_CATEGORY_IDS 			= Value("categoryIds")
	val $PARAM_TARGET_PAGE_FLOW 	= Value("targetPageFlow")  
}