package com.sasaki.math.number

import independent._

/**
 * 
 */
class CharNumber(val $v: String) extends AbstractNumber[CharNumber] {
  
  import regex._
  import Symbol._
  
  type C = CharNumber

  /**
   * 3ab + 2b + 2b -> 4b + 3a + 3ab
   *
   * 3ab + 2b - b
   */
  def parse$V: Seq[Tuple2[Int, String]] =
    if (isExpression) {
      if (isMetaAdd) {
        val item___coefficient = $v.split($_+)                               // 3a + 2b + 2b
          .map { o => (exItem(o), exCoefficient(o)) }                        // (a, 3) (b,2) (b,2)
          .groupBy(o => o._1)                                                // (a, [(a,3)]) (b, [(b,2), (b,2)])  
          .map { case (k, ks_vs) => (k, ks_vs.map(_._2).reduce(_ + _)) }     //

        item___coefficient.values zip item___coefficient.keys toList
      } else
        null
    } else // 3a2b -> 6 
      List((this.coefficient, this.item))
  
  protected def isExpression = this.$v.contains($_+) || this.$v.contains($_-)
  
  protected def isMetaAdd: Boolean = isMetaAdd(this.$v)
  
  protected def coefficient = // 3a2b -> 6
    invokeWithRequire(() => this.isExpression, "Expression will not extrace coefficient.") { () => 
      exCoefficient($v)
    }
  
  protected def item = // 3a2b2b -> ab
    invokeWithRequire(() => this.isExpression, "Expression will not extrace item.") { () => 
      exItem($v)
    }
  
  /**
   * 判断表达式是否仅为原子操作，操作符数仅一个
   * a + b 				-> true
   * a + b + c			-> false
   * a + b - c			-> false
   */
  private def isMetaOperator(s: String) = 
    1 == s.filter(o => o == $_+ || o == $_- || o == $_*).length()
    
  private def isMetaAdd(s: String) = 
    isMetaOperator(s) && 1 == s.filter(o => o == $_+).length()
  
  private def isMetaSub(s: String) = 
    isMetaOperator(s) && 1 == s.filter(o => o == $_-).length()
    
  private def isMetaMult(s: String) = 
    isMetaOperator(s) && 1 == s.filter(o => o == $_*).length()
    
  /**
   * 判断表达式是否为纯加法操作，操作符仅含+
   */
  private def isPureAdd(s: String) =
    s.filter(o => o == $_+ || o == $_-).forall(o => o == $_+)
  
  private def exCoefficient(s: String) = {
    val nums = extractNumbers(s)
    if (nums isEmpty) // default coefficient
      1
    else
      nums.reduce(_ * _)
  }
    
  private def exItem(s: String) = 
    extractNonNumbers(erase($v, $s)).distinct.reduce(_ + _) 
    
  // 2ab + 3a
  override def +(o: C): C = {
//    val coefficient_ = o.coefficient
//    val item_ = o.item
//    println(coefficient_ + " " + item_ + " "+ coefficient)
      
//      {
//      if(item == item_) // 2a and 3a -> 5a
//        s"${coefficient + coefficient_}$item"
//      else // 2a and 3b -> 2a + 3b
//        s"${coefficient}$item + $coefficient_$item_"
//    }
    CharNumber(this.$v + " + " + o.$v)
  }
    
  override def -(n: C): C = ???
  override def *(n: C): C = ???
  override def /(n: C): C = ???
  override def ^(i: Int): C = ???
  protected override def power(num: C, n: Int): C = ???
  
  override def toString = parse$V.map(o => o._1 + o._2).mkString(" + ")
}

object CharNumber {
  def apply($s: String) = new CharNumber($s)
}