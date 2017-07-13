package com.sasaki.lp.poso

import org.squeryl.annotations.{Column, ColumnBase, Transient}

class Base(var taskId: Long) {}

class AreaTop3Product(@ColumnBase("task_id") _taskId: Long) extends Base(_taskId) {
  var area: String = _
  @Column("area_level")
  var areaLevel: String = _
  @Column("product_id")
  var productId: Int = _
  @Column("city_info")
  var cityInfo: String = _
  @Column("click_count")
  var clickCount: Int = _
  @Column("product_name")
  var productName: String = _
  @Column("product_status")
  var productStatus: String = _
}

class PageSplitConvertRate(@ColumnBase("task_id") _taskId: Long) extends Base(_taskId) {
  @Column("convert_rate")
  var convertRate: String = _
}

class SessionAggregationStatus(@ColumnBase("task_id") _taskId: Long) extends Base(_taskId) {
  @Column("session_count")
  var sessionCount: Long = _
  var visit_length_1s_3s_ratio: String = _
  var visit_length_4s_6s_ratio: String = _
  var visit_length_7s_9s_ratio: String = _
  var visit_length_10s_30s_ratio: String = _
  var visit_length_30s_60s_ratio: String = _
  var visit_length_1m_3m_ratio: String = _
  var visit_length_3m_10m_ratio: String = _
  var visit_length_10m_30m_ratio: String = _
  var visit_length_30m_ratio: String = _
  var step_length_1_3_ratio: String = _
  var step_length_4_6_ratio: String = _
  var step_length_7_9_ratio: String = _
  var step_length_10_30_ratio: String = _
  var step_length_30_60_ratio: String = _
  var step_length_60_ratio: String = _
}

class SessionDetail(@ColumnBase("task_id") _taskId: Long) extends Base(_taskId) {
  @Column("user_id")
  var userId: Long = _
  @Column("session_id")
  var sessionId: String = _
  @Column("page_id")
  var pageId: Long = _
  @Column("action_time")
  var actionTime: String = _
  @Column("search_keyword")
  var searchKeyword: String = _
  @Column("click_category_id")
  var clickCategoryId: Long = _
  @Column("click_product_id")
  var clickProductId: Long = _
  @Column("order_category_ids")
  var orderCategoryIds: String = _
  @Column("order_product_ds")
  var orderProductIds: String = _
  @Column("pay_category_ids")
  var payCategoryIds: String = _
  @Column("pay_product_ids")
  var payProductIds: String = _  

}

class SessionRandomExtract(@ColumnBase("task_id") _taskId: Long) extends Base(_taskId) {
  @Column("session_id")
  var sessionId: String = _
  @Column("start_time")
  var startTime: String = _
  @Column("search_keywords")
  var searchKeywords: String = _
  @Column("click_category_ids")
  var clickCategoryIds: String = _
}

class Task(@ColumnBase("task_id") _taskId: Long) extends Base(_taskId) {
  @Column("task_name")
  var taskName: String = _
  @Column("create_time")
  var createTime: String = _
  @Column("start_time")
  var startTime: String = _
  @Column("finish_time")
  var finishTime: String = _
  @Column("task_type")
  var taskType: String = _
  @Column("task_status")
  var taskStatus: String = _
  @Column("task_param")
  var taskParam: String = _
}

class Top10Category(@ColumnBase("task_id") _taskId: Long) extends Base(_taskId) {
  @Column("category_id")
  var categoryId: String = _
  @Column("click_count")
  var clickCount: String = _
  @Column("pay_count")
  var payCount: String = _
  @Column("order_count")
  var orderCount: String = _
}

class Top10Session(@ColumnBase("task_id") _taskId: Long) extends Base(_taskId)  {
  @Column("category_id")
  var categoryId: String = _
  @Column("session_id")
  var sessionId: String = _
  @Column("click_count")
  var clickCount: String = _
  
}