package chapter3

/**
 * 页面访问记录
 *
 * @param id ID
 * @param timestamp 访问时间戳（秒）
 * @param userId 用户ID
 * @param visitUrl 访问的链接
 * @param visitTime 访问停留时间（秒）
 */
case class PageView(id: Int, timestamp: Long, userId: Int, visitUrl:String, visitTime:Int)
