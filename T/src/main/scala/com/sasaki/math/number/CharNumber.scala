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

  def parse$V: Seq[U] =
    if (isExpression)
      valueOfExpression
    else // 3a2b -> 6 
      List((coefficient, item))
  
  def valueOfExpression = parseExpression(self)

  def isExpression =
    $v.contains($_+) ||
    $v.contains($_-) ||
    $v.contains($_*)

  def isUnitAdd = CN.isUnitAdd($v)

  def isUnitMult = CN.isUnitMult($v)

  def isPureAdd = CN.isPureAdd($v)

  def isPureMult = CN.isPureMult($v)
  
  /**
   *  3a2b -> 6
   */
   def coefficient =
    invokeWithRequire(() => isExpression, "Expression will not extrace coefficient.") { () => 
      exUnitCoefficient($v)
    }
  
  /**
   * 3a2b2b -> ab
   */
   def item =
    invokeWithRequire(() => isExpression, "Expression will not extrace item.") { () => 
      exUnitItem($v)
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
  private[number] type U = Tuple2[Int/*coefficient*/, String/*item*/]
  private[number] type PIAR_OPERATOR = 
    Tuple3[String/*item left*/, String/*operator*/, String/*item right*/]
  
  private val MUST_BE_WITCH_OPERATOR = (s: Symbol) => s"Express muse be unit $s operator!"
  
  /**
   * 纯加法：
   * 3ab + 2b + 2b 		-> 4b + 3a + 3ab
   * 纯乘法：
   * 3ab * 2b * 2b	 		-> 12abbb
   *
   * 3ab + 2b - b + ab - b
   */
  private def parseExpression(self: C): Seq[U] =
    if (self.isPureAdd) {
      val item___coefficient = self.$v.split($_+)                               // 3a + 2b + 2b
        // 倒序 项___系数，避免系数为key时map元素丢失
        .map(o => (exUnitItem(o), exUnitCoefficient(o)))                        // (a, 3) (b,2) (b,2)
        .groupBy(_._1)                                                          // (a, [(a,3)]) (b, [(b,2), (b,2)])  
        .map { case (k, ks_vs) => (k, ks_vs.map(_._2).reduce(_ + _)) }          //

      item___coefficient.values zip item___coefficient.keys toList
    } else if(self.isPureMult) {                                                // 3a * 2b * 2b
      val coefficient___item = self.$v.split($_*)                               // (a, 3) (b,2) (b,2)
        .map(o => (exUnitItem(o), exUnitCoefficient(o)))                      
        .reduce((_o, o_) => (_o._1 + o_._1, _o._2 * o_._2))

      Seq((coefficient___item._2, coefficient___item._1))
    } else {
      val unitPair = exUnitPairOperator(self.$v) 
      
      def loop(pair: Seq[U], i: Int/*, buf: String*/): Seq[U] = {
        if(i == pair.size - 1) {
          
        }
        Seq()
      }
      
      Seq()
    }
  
  /**
   * 3ab + 2b - b      
   */
   def exUnitPairOperator(s: String): Seq[PIAR_OPERATOR] = {
    val numbers = s.split(symbols).map(trimSpace _)
    val _numbers = body(numbers)
    val numbers_ = numbers.tail
    val operators = exOperator(s).split($e)
    for(i <- 0 until operators.length) yield (_numbers(i), operators(i), numbers_(i))
  }
        
  /**
   * 判断表达式是否仅为原子操作，操作符数仅一个
   * a + b 				-> true
   * a + b + c			-> false
   * a + b - c			-> false
   */
  private def isUnitOperator(s: String) = 1 == s.filter(isOperator _).length()
    
  private def isUnitAdd(s: String) =
    isUnitOperator(s) && 1 == s.filter(o => o == $_+).length()

  private def isUnitSub(s: String) =
    isUnitOperator(s) && 1 == s.filter(o => o == $_-).length()

  private def isUnitMult(s: String) =
    isUnitOperator(s) && 1 == s.filter(o => o == $_*).length()
    
  /**
   * 计算单位加法
   * 3a + 2b 		= 3a + 2b
   * 3a + 2a 		= 5a
   * 3ab + ba 		= 4ab
   */
  private def unitAdd(_c_i: U, c_i: U): String = {
    val _coefficient = _c_i._1
    val coefficient_ = c_i._1
    val _item = _c_i._2
    val item_ = _c_i._2

    if (equalItem(_item, item_))
      _coefficient + coefficient_ + _item
    else
      formatUnit(_c_i) + " + " + formatUnit(c_i)
  }

  private def exUnitCoefficient(s: String) = {
    val nums = extractNumbers(s)
    if (nums isEmpty) // default coefficient
      1
    else
      nums.reduce(_ * _)
  }

  /**
   * 
   */
  private def exUnitItem(s: String) =
    extractNonNumbers(trimSpace(s)).distinct.reduce(_ + _)
    
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

  private def trimSpace(s: String) = erase(s, $s)
  
  /**
   * 格式化一个单位 系数_项 无组
   * (1, "a")		-> a
   * (2, "a")		-> 2a
   */
  private def formatUnit(u: U): String = if(1 == u._1) u._2 else u._1 + u._2
  
  /**
   * 判断两个项等价
   * ab && ab && ba 		-> true
   * ab && b						-> false
   */
  private def equalItem(_s: String, s: String) = {
    lazy val _ss = _s.split($e).distinct
    lazy val ss = s.split($e).distinct
    if ({
      // 字符串长度
      _s.length() != s.length() ||
      // 去重后字符数组长度
      _ss.size != ss.size
    })
      false
    else
      // _ss 中每个字符在 ss 中皆存在
      _ss.forall(ss contains _)
  }
  
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
      n5
    
    }
  }
}
