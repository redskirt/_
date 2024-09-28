package chapter7


/**
 * 订单事件
 * @param userId 用户ID
 * @param orderId 订单ID
 * @param eventType 事件类型
 * @param timestamp 时间戳
 */
case class OrderEvent(userId: String, orderId: String, eventType: String, timestamp: Long)
