package com.sasaki.callback.func

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-30 上午10:57:55
 * @Description 
 */
class TestFunc {
  
}

object TestFunc {
  // 1. 高阶函数不带参数
  def sum(f: (Int, Int) => Int): Int = f(1, 2)
  
  // 2. 高阶函数带参数
  // 定义计算函数
  def avg(list: List[Int], factor: Int): Double = {
    if(list == Nil || list.isEmpty || factor == 0)
      0.0
    else
      list.sum / factor
  }

  // 匿名函数
  val avg_ = (list: List[Int], factor: Int) => {
    if (list == Nil || list.isEmpty || factor == 0)
      0.0
    else
      list.sum / factor
  }
  
  // 高阶函数传入计算
  def execute(list: List[Int], factor: Int, fx: (List[Int], Int) => Double) = {
    fx(list, factor)
  }
  
  // 柯里化
  def execute_(list: List[Int], factor: Int)(fx: (List[Int], Int) => Double) = {
    fx(list, factor)
  }
  
  // 3. 函数作为返回值
  // 返回匿名函数
  def fxAsReturn(s: String) = (o: Int, o_ : Int) => s + "sum: " + o + o_
  
  // 返回已定义的函数 ???
//  def fxAsReturn(list: List[Int], factor: Int) = avg(list, factor)
  
  def main(args: Array[String]): Unit = {
//    println(sum((o, o_) => o * o_))
    
//    println(execute(List(1, 2, 4, 5), 4, avg))
//    println(execute_(List(1, 2, 4, 5), 4)(avg_))
//    println((3.toDouble/8).toDouble)
    println(List(2, 4, 2, 5).foldLeft(1)((x, y) => x/y))
  }
}