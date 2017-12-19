package com.sasaki.math.number

import independent._
import regex._
import Symbol._
import com.sasaki.math.number.{ CharNumber => CN}
import com.sasaki.math.number.{ UnitNumber => UN, UnitOperator => UO }
import scala.collection.mutable.MapBuilder

/**
 * 
 */
class CharNumber(val $v: String) extends AbstractNumber[CharNumber] {
  
  import CharNumber._

  val self = this 

  def parseValue: Seq[UN] =
    if (isOperator)
      null//valueOfOperator
    else
      List(UN(coefficient, item))
  
  def valueOfOperator = parseOperator(self)

  def isOperator =
    $v.contains($_+) ||
    $v.contains($_-) ||
    $v.contains($_*)

  def isPure_+&- = CN.isPure_+&-($v)

  def isPure_* = CN.isPure_*($v)
  
  /**
   *  3a2b -> 6
   */
   def coefficient =
    invokeWithRequire(() => isOperator, "Operator will not extrace coefficient.") { () => 
      exUnitCoefficient($v)
    }
  
  /**
   * 3a2b2b -> ab
   */
   def item =
    invokeWithRequire(() => isOperator, "Operator will not extrace item.") { () => 
      exUnitItem($v)
    }
    
  /**
   * 3a2b2b -> ab
   */
  override def +(o: C): C = {
    CharNumber(self.$v + " + " + o.$v)
  }
    
  override def -(n: C): this.type = ???
  override def *(n: C): C = ???
  override def /(n: C): C = ???
  override def ^(i: Int): C = ???
  protected override def power(num: C, n: Int): C = ???
  
  override def toString = parseValue.map(o => o.coefficient + o.item).mkString(" + ")
}

object CharNumber {
  
  private[number] type C = CharNumber
  private[number] type UN = UnitNumber 
  //Tuple2[Int/*coefficient*/, String/*item*/]
  private[number] type UO = UnitOperator
//    Tuple3[String/*item left*/, String/*operator*/, String/*item right*/]
  
  private val MUST_BE_WITCH_OPERATOR = (s: Symbol) => s"Express muse be unit $s operator!"
  
  private def parseOperator(self: C): Option[Seq[UN]] = ???
//    if (self.isPure_+&-)
      
  
  /**
   * 3ab + 2b - b      
   */
   def exUnitPairOperator(s: String): Option[Seq[UN]] = {
    
    s
    .split(symbols)
    .map{ o => 
      val s = trimSpace(o)
      val coefficient = exUnitCoefficient(s)
      val item = exUnitItem(s)
      UN(coefficient, item)
    }
    

     None
  }

  /**
   * 计算组合纯加减法
   * 3a
   * [3a] 								= [3a]
   * 
   * 3a - 2a
   * [3a, 2a] 						= [a] 
   * 
   * 3ab + 2ba
   * [3ab, 2ba]	 		 			= [5ab]
   * 
   * 3a + 2b - 2c - b
   * [3a, 2b, -2c, -b]	  = [3a, -2c, b]
   */
  def pure_+&-(uns: Seq[UN]): Option[Seq[UN]] = 
    if (uns == null || uns.isEmpty)
      None
    else if (1 == uns.size)
      Some(List(uns.head))
    else {
      val result =
        uns
          .map { case UN(coefficient, item) =>
              UN(coefficient, item.split($e).sorted.mkString)
          }
          .groupBy(_.item)
          .map(o => UN(o._2.map(_.coefficient).reduce(_ + _)/*coefficients*/, o._1))
          .filterNot(0 == _.coefficient)
          .toList
      if (result nonEmpty)
        Some(result)
      else
        None
    }
  
  /**
   * 
   */
  private def parseString2UnitNumber(s: String) = UN(exUnitCoefficient(s), exUnitItem(s))

  private def exUnitCoefficient(s: String) = {
    val nums = extractNumbers(s)
    if (nums isEmpty)
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
   * 判断表达式是否为纯加减法操作，操作符仅含+
   */
  private def isPure_+&-(s: String) = exOperator(s).forall(o => o == $_+ || o ==$_-)
    
  /**
   * 判断表达式是否为纯乘法操作，操作符仅含*
   */
  private def isPure_*(s: String) = exOperator(s).forall(o => o == $_*)

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
  
  def apply($s: String) = new CharNumber($s)
}

abstract class AbstractUnitNumber(val coefficient: Int, val item: String)

/**
 * 单位元组，表示一个 系数_项
 */
class UnitNumber(override val coefficient: Int, override val item: String)
  extends AbstractUnitNumber(coefficient, item) {
  
  val self = this
  
//  if (0 == self.coefficient)
//    UN(0, $e)
//  else
//    UN(self.coefficient, self.item)
        
  override def toString = 
    if(0 == coefficient) 
      $e 
    else if(1 == coefficient) 
      item
    else 
      coefficient + item
}

object UnitNumber {
  def apply(coefficient: Int, item: String) = new UN(coefficient, item)
  def unapply(un: UN) = Option(un.coefficient, un.item)
}

/**
 * 单位操作，表示一个 数值1_符号_数值2
 */
class UnitOperator(val _1: UN, val symbol: Symbol, val _2: UN)
  extends UN(_1.coefficient, _1.item) {

  def this(_1: UN) = this(_1, null, null)

  val _coefficient = _1.coefficient
  val coefficient_ = _2.coefficient
  val _item = _1.item
  val item_ = _2.item

  /**
   * 计算单位加法
   * 3a + 2b 		= 3a + 2b
   * 3a + 2a 		= 5a
   * 3ab + ba 	= 4ab
   */
  def unit_+[T <: AbstractUnitNumber]: T =
    if (equalItem)
      UN(_coefficient + coefficient_, _item).asInstanceOf[T]
    else
      UO(_1, Symbol.+, _2).asInstanceOf[T]

  /**
   * 计算单位减法
   * 3a - 2b 		= 3a - 2b
   * 3a - 2a 		= a
   * 3ab - ba 	= 4ab
   */
  def unit_-[T <: AbstractUnitNumber]: T =
    if (equalItem)
      UN(_coefficient - coefficient_, _item).asInstanceOf[T]
    else
      UO(_1, Symbol.-, _2).asInstanceOf[T]

  /**
   * 计算单位元组表达式的值
   */
  def valueOfUnit[T]: T = symbol match {
    case + => unit_+
    case - => unit_-
    case _ => ???
  }

  /**
   * 判断两个单位项等价，仅判断除系数部分
   * ab && ab && ba 		-> true
   * ab && b						-> false
   */
  private def equalItem = equalString(_1.item, _2.item)

}

object UnitOperator {
  def apply(_1: UN, symbol: Symbol, _2: UN) = new UO(_1, symbol, _2)
  def apply(_1: UN) = new UO(_1)
}

object Main {
  
  def main(args: Array[String]): Unit = {
    val n1 = new PureNumber(123)
    val n2 = new PureNumber(2)
    
    val n3 = CN("3ab + 2b + 2b + a +d")
    val n4 = CN("a")
    val n5 = CN("3a + 3ab + 2b + 2b")
    val n6 = CN("3ab * 2b * 2b")
    
    val n7 = UO(UN(3, "ab"), Symbol.+, UN(1, "ba"))
    
    val ln3 = List(n3)
    val ln7 = List(n7)
    
    val uns = List(UN(0, ""), UN(0, "ba"), UN(0, "ab"), UN(0, ""))
    
    println {
//      n3 
      
      CharNumber.pure_+&-(uns)
      
    }
  }
}
