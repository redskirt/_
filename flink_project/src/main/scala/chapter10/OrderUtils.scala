package chapter10

object OrderUtils {
  /**
   * 读取字符串，转换为订单对象
   * @param strOrderInfo 字符串
   * @return 订单对象
   */
  def read(strOrderInfo: String): OrderInfo = {

    //拆分字符串
    val arrOrderInfo = strOrderInfo.split(",")
    //创建时间
    val createTime = arrOrderInfo(0)
    //拆分为拆分日期和时间
    val arrCreateTime = createTime.split(" ")
    //创建日期
    val createDate = arrCreateTime(0)
    //从创建时间中获取小时
    val createHour = arrCreateTime(1).split("-")(0).split(":")(0).toInt
    //付款时间
    val payTime = arrOrderInfo(1)
    //订单金额
    val orderPrice = arrOrderInfo(2).toFloat
    //付款金额
    val payPrice = arrOrderInfo(3).toFloat
    //退款金额
    val returnPrice = arrOrderInfo(4).toFloat
    //地址
    val addr = arrOrderInfo(5)

    //构造对象
    OrderInfo(createTime, createDate, createHour, payTime, orderPrice, payPrice, returnPrice, addr)

  }

}
