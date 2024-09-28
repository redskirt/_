package chapter9


/**
 * 访问日志
 * @param ts 时间戳
 * @param host 主机名
 * @param clientip 客户端IP
 * @param request 请求
 * @param httpUserAgent 用户代理
 * @param size 请求的大小
 * @param responseTime 响应时间
 * @param httpHost 主机
 * @param url URL
 * @param domain 域名
 * @param referer 来源
 * @param status 状态码
 */
case class AccessLog(createTime:String,createDate:String,createHour:Int,host:String,clientip:String,request:String,httpUserAgent:String,size:Int,responseTime:Float,httpHost:String,url:String,domain:String,referer:String,status:Int)
