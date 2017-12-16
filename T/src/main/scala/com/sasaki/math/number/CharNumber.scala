package com.sasaki.math.number

import independent._
import regex._
import Symbol._

/**
 * 
 */
class CharNumber(val $v: String) extends AbstractNumber[CharNumber] {
  
  import com.sasaki.math.number.{ CharNumber => CN }
  import CharNumber._

  val self = this
  
  /**
   * 纯加法：
   * 3ab + 2b + 2b 		-> 4b + 3a + 3ab
   * 纯乘法：
   * 3ab * 2b * 2b	 		-> 12abbb
   *
   * 3ab + 2b - b
   */
  def parse$V: Seq[META] =
    if (isExpression)
      valueOfExpression
    else // 3a2b -> 6 
      List((coefficient, item))
  
  def valueOfExpression = parseExpression(self)

  def isExpression =
    $v.contains($_+) ||
    $v.contains($_-) ||
    $v.contains($_*)

  def isMetaAdd = CN.isMetaAdd($v)

  def isMetaMult = CN.isMetaMult($v)

  def isPureAdd = CN.isPureAdd($v)

  def isPureMult = CN.isPureMult($v)
  
  /**
   *  3a2b -> 6
   */
   def coefficient =
    invokeWithRequire(() => isExpression, "Expression will not extrace coefficient.") { () => 
      exMetaCoefficient($v)
    }
  
  /**
   * 3a2b2b -> ab
   */
   def item =
    invokeWithRequire(() => isExpression, "Expression will not extrace item.") { () => 
      exMetaItem($v)
    }
    
  /**
   * 3a2b2b -> ab
   */
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
    CharNumber(self.$v + " + " + o.$v)
  }
    
  override def -(n: C): this.type = ???
  override def *(n: C): C = ???
  override def /(n: C): C = ???
  override def ^(i: Int): C = ???
  protected override def power(num: C, n: Int): C = ???
  
  override def toString = parse$V.map(o => o._1 + o._2).mkString(" + ")
}

object CharNumber {
  
  private[number] type C = CharNumber
  private[number] type META = Tuple2[Int/*coefficient*/, String/*item*/]

  private def parseExpression(self: C) =
    if (self.isPureAdd) {
        val item___coefficient = self.$v.split($_+)                               // 3a + 2b + 2b
          // 倒序 项___系数，避免系数为key时map元素丢失
          .map(o => (exMetaItem(o), exMetaCoefficient(o)))                        // (a, 3) (b,2) (b,2)
          .groupBy(o => o._1)                                                     // (a, [(a,3)]) (b, [(b,2), (b,2)])  
          .map { case (k, ks_vs) => (k, ks_vs.map(_._2).reduce(_ + _)) }          //

        item___coefficient.values zip item___coefficient.keys toList
      } else if(self.isPureMult) {                                                // 3a * 2b * 2b
        val coefficient___item = self.$v.split($_*)                               // (a, 3) (b,2) (b,2)
          .map(o => (exMetaItem(o), exMetaCoefficient(o)))                      
          .reduce((_o, o_) => (_o._1 + o_._1, _o._2 * o_._2))

        Seq((coefficient___item._2, coefficient___item._1))
      } else 
        ???
        
  /**
   * 判断表达式是否仅为原子操作，操作符数仅一个
   * a + b 				-> true
   * a + b + c			-> false
   * a + b - c			-> false
   */
  private def isMetaOperator(s: String) =
    1 == s.filter(o => o == $_+ || o == $_- || o == $_*).length()
   
  /**
   * 判断字符是否为任意操作符类型  
   */
  private def isOperator(o: Char) = 
    o == $_+ || 
    o == $_- || 
    o == $_*
    
  private def isMetaAdd(s: String) =
    isMetaOperator(s) && 1 == s.filter(o => o == $_+).length()

  private def isMetaSub(s: String) =
    isMetaOperator(s) && 1 == s.filter(o => o == $_-).length()

  private def isMetaMult(s: String) =
    isMetaOperator(s) && 1 == s.filter(o => o == $_*).length()

  private def exMetaCoefficient(s: String) = {
    val nums = extractNumbers(s)
    if (nums isEmpty) // default coefficient
      1
    else
      nums.reduce(_ * _)
  }

  /**
   * 
   */
  private def exMetaItem(s: String) =
    extractNonNumbers(erase(s, $s)).distinct.reduce(_ + _)
    
  /**
   * 提取所有操作符  
   */
  private def exOperator(s: String) = s.filter(isOperator _)
    
  /**
   * 12a*b*b -> 12ab^2
   */
  // TODO
  private def mult2Power(s: String) = {  }
  
  /**
   * 12ab^2 -> 12a*b*b
   */
  // TODO
  private def power2Mult(s: String) = {}
  
  /**
   * 判断表达式是否为纯加法操作，操作符仅含+
   */
  private def isPureAdd(s: String) = exOperator(s).forall(o => o == $_+)
    
  /**
   * 判断表达式是否为纯乘法操作，操作符仅含*
   */
  private def isPureMult(s: String) = exOperator(s).forall(o => o == $_*)

  def apply($s: String) = new CharNumber($s)
}

object Main {
    
  def main(args: Array[String]): Unit = {
    val n1 = new PureNumber(123)
    val n2 = new PureNumber(2)
    
    val n3 = CharNumber("3ab + 2b + 2b + a +d")
    val n4 = CharNumber("a")
    val n5 = CharNumber("3a + 2b + 2b")
    val n6 = CharNumber("3ab * 2b * 2b")
    
    println {
     // n1 + n2
//     n3 + n4
//      n3
      n6
    }
  }
}
