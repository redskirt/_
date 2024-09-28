package chapter10

/**
 * 订单
 * @param createTime 创建时间
 * @param createDate 创建日期
 * @param createHour 创建小时
 * @param payTime 付款时间
 * @param orderPrice 订单金额
 * @param payPrice 付款金额
 * @param returnPrice 退款金额
 * @param addr 地址
 */
case class OrderInfo (createTime:String,createDate:String,createHour:Int,payTime:String,orderPrice:Float,payPrice:Float,returnPrice:Float,addr:String)
