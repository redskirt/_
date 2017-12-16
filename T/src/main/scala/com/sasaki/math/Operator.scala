package com.sasaki.math

import scala.math
import independent._

/**
 * @Author Sasaki
 */

sealed trait Number[T <: Number[_]] {
  
  def +(n: T): T
  
  def -(n: T): T
  
  def *(n: T): T
  
  def /(n: T): T
  
  def ^(n: Int): T
  
  protected def power(n: T, i: Int): T
}

abstract class AbstractNumber[T <: AbstractNumber[_]] extends Number[T] 

class PureNumber(val $v: Int) extends AbstractNumber[PureNumber] {
  type P = PureNumber
  override def +(o: P): P = PureNumber($v + o.$v)
  override def -(o: P): P = PureNumber($v - o.$v)
  override def *(o: P): P = PureNumber($v * o.$v)
  override def /(o: P): P = PureNumber($v / o.$v)
  override def ^(i: Int): P = power(PureNumber($v), i)

  protected override def power(num: P, n: Int): P = {
    @annotation.tailrec
    def loop(num_ :Int, n_ : Int, acc: Int): Int =
      if (0 == n_)
        1
      else if (1 == n_)
        acc
      else
        loop(num_ * acc, n_ - 1, num_ * num.$v)

    val v = loop(num.$v, math.abs(n), 1)
    PureNumber({ if (n >= 0) v else 1 / v })
  }

  //  implicit def Int2PureNumber(value: Int) = new PureNumber(value)
  override def toString = $v.toString()
}

object PureNumber {
  def apply($v: Int) = new PureNumber($v)
}

class CharNumber(val $v: String) extends AbstractNumber[CharNumber] {
  
  import regex._
  import Symbol._
  
  type C = CharNumber

  /**
   * 3ab + 2b + 2b -> 4b + 3a + 3ab
   */
  def parse$V: Seq[Tuple2[Int, String]] = 
    if (isExpression) {                                                 
      val item___coefficient = $v.split($_+)                                // 3a + 2b + 2b
        .map { o => (exItem(o), exCoefficient(o)) }                         // (a, 3) (b,2) (b,2)
        .groupBy(o => o._1)                                                 // (a, [(a,3)]) (b, [(b,2), (b,2)])  
        .map { case (k, ks_vs) => (k, ks_vs.map(_._2).reduce(_ + _)) }      //
        
        item___coefficient.values zip item___coefficient.keys toList
      }
    else                                                                    // 3a2b -> 6 
      List((this.coefficient, this.item))
  
  protected def isExpression = $v.contains($_+) || $v.contains($_-)
  
  protected def coefficient = // 3a2b -> 6
    invokeWithRequire(() => this.isExpression, "Expression will not extrace coefficient.") { () => 
      exCoefficient($v)
    }
  
  protected def item = // 3a2b2b -> ab
    invokeWithRequire(() => this.isExpression, "Expression will not extrace item.") { () => 
      exItem($v)
    }
  
    
  private def exCoefficient(s: String) = {
    val nums = extractNumbers(s)
    if (nums isEmpty) // default coefficient is 1
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

//class CharNumber(s: String) extends Number
//object ImplicitAdapter {
//  implicit final class Int2StringAdd(private val self: Int) extends AnyVal {
//    def +(other: String): String = String.valueOf(self) + other
//  }
//}

object Symbol extends Enumeration {
  type Symbol = Value
  val + = Value("+")  
  val - = Value("-")  
  val * = Value("*")  
  val / = Value("/")  
  val ^ = Value("^")  
  
  val $_+ = "\\+"
  val $_- = "\\-"
}

object Main {
  import scala.math
    
  def main(args: Array[String]): Unit = {
    val n1 = new PureNumber(123)
    val n2 = new PureNumber(2)
    
    val n3 = CharNumber("3ab + 2b + 2b + a +d")
    val n4 = CharNumber("a")
    val n5 = CharNumber("a + 3c + 2b + 2b")
    
    println {
     // n1 + n2
     n3 + n4
//      n3
//      n5
    }
    
  }
}

