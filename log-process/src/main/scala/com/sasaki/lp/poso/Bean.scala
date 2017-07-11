package com.sasaki.lp.poso

class T(taskId: Long) {}

class AreaTop3Product(_taskId: Long) extends T(_taskId) {
  var area: String = _
  var areaLevel: String = _
  var productId: Int = _
  var cityInfo: String = _
  var clickCount: Int = _
  var productName: String = _
  var productStatus: String = _
}

class PageSplitConvertRate(_taskId: Long) extends T(_taskId) {
  var convertRate: String = _
}

class SessionAggregationStatus(_taskId: Long) extends T(_taskId) {
  var session_count: Long = _
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

class SessionDetail(_taskId: Long) extends T(_taskId) {
  var userid: Long = _
  var sessionid: String = _
  var pageid: Long = _
  var actionTime: String = _
  var searchKeyword: String = _
  var clickCategoryId: Long = _
  var clickProductId: Long = _
  var orderCategoryIds: String = _
  var orderProductIds: String = _
  var payCategoryIds: String = _
  var payProductIds: String = _
}

class SessionRandomExtract(_taskId: Long) extends T(_taskId) {
  var sessionid: String = _
  var startTime: String = _
  var searchKeywords: String = _
  var clickCategoryIds: String = _
}

class Task(_taskId: Long) extends T(_taskId) {
  var taskName: String = _
  var createTime: String = _
  var startTime: String = _
  var finishTime: String = _
  var taskType: String = _
  var taskStatus: String = _
  var taskParam: String = _  
}

class Top10Category(_taskId: Long) extends T(_taskId) {
  var categoryid: String = _
  var clickCount: String = _
  var payCount: String = _
  var orderCount: String = _
}

class Top10Session {
  var categoryid: String = _
  var sessionid: String = _
  var clickCount: String = _
}