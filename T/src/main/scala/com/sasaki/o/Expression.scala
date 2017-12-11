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
    
    val list1: Seq[Int] = for(i <- 1 to 5) yield i  
    val list2: Seq[Int] = Seq(1, 2, 3, 4, 5)  
     
  }
  
}