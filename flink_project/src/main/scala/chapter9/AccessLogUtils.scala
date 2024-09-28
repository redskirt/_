package chapter9

import com.alibaba.fastjson.{JSON, JSONObject}

object AccessLogUtils {
  /**
   * 读取JSON，转换为AccessLog
   *
   * @param strAccessLog JSON字符串
   * @return AccessLog对象
   */
  def read(strAccessLog: String): AccessLog = {
    //解析为JSONObject
    val jsonObj: JSONObject = JSON.parseObject(strAccessLog)
    //读取信息
    val timestamp: String = jsonObj.getString("@timestamp")

    //拆分为拆分日期和时间
    val arrCreateTime = timestamp.split("T")
    //日期
    val createDate = arrCreateTime(0)
    //从时间中获取小时
    val
    createHour = arrCreateTime(1).split("-")(0).split(":")(0).toInt
    val host: String = jsonObj.getString("host")
    val clientip: String = jsonObj.getString("clientip")
    val request: String = jsonObj.getString("request")
    val httpUserAgent: String = jsonObj.getString("httpUserAgent")
    val size: Int = jsonObj.getIntValue("size")
    val responseTime: Float = jsonObj.getFloatValue("responsetime")
    val httpHost: String = jsonObj.getString("httpHost")
    val url: String = jsonObj.getString("url")
    val domain: String = jsonObj.getString("domain")
    val referer: String = jsonObj.getString("referer")
    val status: Int = jsonObj.getIntValue("status")
    //构造AccessLog对象
    AccessLog(timestamp,createDate, createHour,host, clientip, request, httpUserAgent, size, responseTime, httpHost, url, domain, referer, status)
  }

}
