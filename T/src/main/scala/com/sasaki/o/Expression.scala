package com.sasaki.o

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-12-11 上午10:57:10
 * @Description 
 */
object Expression {
  
  def main(args: Array[String]): Unit = {
    val flag = if(true) 0 else 1 
  
    val s = "abc"
    val sSub = if(s.nonEmpty) s.substring(1) else "" 
    
    // for-yield 直接返回一个可循环的类型，表示for-yield是带返回的
    val list1: Seq[Int] = for(i <- 1 to 5) yield i  
    // 更实用的声明序列的情况
    val list2: Seq[Int] = Seq(1, 2, 3, 4, 5)  
    // 如果不使用yiled，for的返回类型为Unit
    val list3: Unit = for(i <- 1 to 5) i
     
    // 在参数位置传入一个表达式，忽略这里的恒为真情况
    show(if(true) "is ture." else "is trouble.")
  }
    def show(s: String) = println(s)
  
}