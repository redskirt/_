package chapter7

/**
 * 登录事件
 *
 * @param userId    用户ID
 * @param ipAddr    IP地址
 * @param eventType 事件类型，success表示登录成功，fail表示登录失败
 * @param timestamp 登录时间戳
 */
case class LoginEvent(userId: String, ipAddr: String, eventType: String, timestamp: Long)