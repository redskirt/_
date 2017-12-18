package com.sasaki.math.number

import independent._
import regex._
import Symbol._
import com.sasaki.math.number.{ CharNumber => CN, UnitNumber => UN, UnitOperator => UO }

/**
 * 
 */
class CharNumber(val $v: String) extends AbstractNumber[CharNumber] {
  
  import CharNumber._

  val self = this 

  def parse$V: Seq[UN] =
    if (isExpression)
      valueOfExpression
    else // 3a2b -> 6 
      List(UN(coefficient, item))
  
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
  
  override def toString = parse$V.map(o => o.coefficient + o.item).mkString(" + ")
}

object CharNumber {
  
  private[number] type C = CharNumber
  private[number] type UN = UnitNumber //Tuple2[Int/*coefficient*/, String/*item*/]
  private[number] type UO = UnitOperator
//    Tuple3[String/*item left*/, String/*operator*/, String/*item right*/]
  
  private val MUST_BE_WITCH_OPERATOR = (s: Symbol) => s"Express muse be unit $s operator!"
  
  /**
   * 纯加法：
   * 3ab + 2b + 2b 		-> 4b + 3a + 3ab
   * 纯乘法：
   * 3ab * 2b * 2b	 		-> 12abbb
   *
   * 3ab + 2b - b + ab - b
   */
  private def parseExpression(self: C): Seq[UN] =
    if (self.isPureAdd) {
      self.$v.split($_+)                               // 3a + 2b + 2b
        // 倒序 项___系数，避免系数为key时map元素丢失
        .map(o => (exUnitItem(o), exUnitCoefficient(o)))                        // (a, 3) (b,2) (b,2)
        .groupBy(_._1)                                                          // (a, [(a,3)]) (b, [(b,2), (b,2)])  
        .map { case (k, ks_vs) => UN(ks_vs.map(_._2).reduce(_ + _), k) }          //
        .toList
    } else if(self.isPureMult) {                                                // 3a * 2b * 2b
      val singleU = self.$v.split($_*)                               // (a, 3) (b,2) (b,2)
        .map(o => UN(exUnitCoefficient(o), exUnitItem(o)))                      
        .reduce((_o, o_) => UN(_o.coefficient * o_.coefficient, _o.item + o_.item))

      List(singleU)
    } else {
      // 3ab + 2b - b + ab - b
      // [(3ab, +, 2b), (2b, -, b), (b, +, ab), (ab - b)]
      val unitPair = exUnitPairOperator(self.$v) 
      
      def loop(pair: Seq[UO], i: Int/*, buf: String*/): Seq[UO] = {
        if(i == pair.size - 1) { // 仅 [(3ab, +, 2b)]
          
        }
        Seq()
      }
      
      loop(unitPair, 0)
      
      Seq()
    }
  
  /**
   * 3ab + 2b - b      
   */
   def exUnitPairOperator(s: String): Seq[UO] = {
    val numbers = s.split(symbols).map(trimSpace _)
    val _numbers = body(numbers)
    val numbers_ = numbers.tail
    val operators = exOperator(s).split($e)
    for(i <- 0 until operators.length) yield 
      UO(parseString2UnitNumber(_numbers(i)), withName(operators(i)), parseString2UnitNumber(numbers_(i)))
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
   * 3ab + ba 	= 4ab
   */
   def unitAdd[T <: AbstractUnitNumber](_c_i: UN, c_i: UN): T = {
    val _coefficient = _c_i.coefficient
    val coefficient_ = c_i.coefficient
    val _item = _c_i.item
    val item_ = c_i.item
    
    if (equalItem(_item, item_)) 
      UN(_coefficient + coefficient_, _item).asInstanceOf[T]
    else
      UO(_c_i, Symbol.+, c_i).asInstanceOf[T]
  }
  
  /**
   * 由单位元组计算加法
   */
   def unitAdd[T <: AbstractUnitNumber](o: UO): T = unitAdd(o._1, o._2)
  
  /**
   * 
   */
  private def parseString2UnitNumber(s: String) = UN(exUnitCoefficient(s), exUnitItem(s))

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
  private def mult2Power(s: String) = ???
  
  /**
   * 12ab^2 -> 12a*b*b
   */
  // TODO
  private def power2Mult(s: String) = ???
  
  /**
   * 判断表达式是否为纯加法操作，操作符仅含+
   */
  private def isPureAdd(s: String) = exOperator(s).forall(o => o == $_+)
    
  /**
   * 判断表达式是否为纯乘法操作，操作符仅含*
   */
  private def isPureMult(s: String) = exOperator(s).forall(o => o == $_*)

  /**
   * 
   */
  private def trimSpace(s: String) = erase(s, $s)
  
  /**
   * 格式化一个单位元组 系数_项
   * (1, "a")		-> a
   * (2, "a")		-> 2a
   */
  private def formatUnit(u: UN) = if(1 == u.coefficient) u.item else u.coefficient + u.item
  
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

abstract class AbstractUnitNumber(
 val coefficient: Int,
 val item: String
)

/**
 * 单位元组，表示一个 系数_项
 */
case class UnitNumber(
    override val coefficient: Int, 
    override val item: String) 
  extends AbstractUnitNumber(coefficient, item)

/**
 * 单位操作，表示一个 数值1_符号_数值2
 */
case class UnitOperator(
    _1: UnitNumber, 
    symbol: Symbol, 
    _2: UnitNumber) 
  extends AbstractUnitNumber(_1.coefficient, _1.item)

object Main {
  
  def main(args: Array[String]): Unit = {
    val n1 = new PureNumber(123)
    val n2 = new PureNumber(2)
    
    val n3 = CN("3ab + 2b + 2b + a +d")
    val n4 = CN("a")
    val n5 = CN("3a + 3ab + 2b + 2b")
    val n6 = CN("3ab * 2b * 2b")
    
    val n7 = UO(UN(3, "b"), Symbol.+, UN(1, "b"))
    
    println {
     // n1 + n2
//     n3 + n4
//      n3 
      CN.unitAdd(n7)
    
    }
  }
}
