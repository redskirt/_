package com.sasaki.math

import scala.math
/**
 * @Author Sasaki
 */

sealed trait Number[T <: Number[_]] {
//  def +(n: T): T
//  def -(n: T): T
//  def *(n: T): T
//  def /(n: T): T
//  def ^(n: Int): T
  
  protected def power(n: T, i: Int): T
}

abstract class AbstractNumber[T <: AbstractNumber[_]] extends Number[T] {
  def +(n: T): this.type
  def -(n: T): T
  def *(n: T): T
  def /(n: T): T
  def ^(n: Int): T
}

class PureNumber(val $v: Int) extends AbstractNumber[PureNumber] {
  type P = PureNumber
  override def +(n: P): P = PureNumber($v + n.$v)
  override def -(n: P): P = PureNumber($v - n.$v)
  override def *(n: P): P = PureNumber($v * n.$v)
  override def /(n: P): P = PureNumber($v / n.$v)
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

class CharNumber($s: String) extends AbstractNumber[CharNumber] {
  type C = CharNumber
  override def +(n: C): C = {
    
    
    null
  }
//  override def -(n: P): P = CharNumber($s - n.$s)
//  override def *(n: P): P = CharNumber($s * n.$s)
//  override def /(n: P): P = CharNumber($s / n.$s)
//  override def ^(i: Int): P = power(CharNumber($v), i)
  
  
}

//class CharNumber(s: String) extends Number

object Symbol extends Enumeration {
  type Symbol = Value
  val + = Value("+")  
  val - = Value("-")  
  val * = Value("*")  
  val / = Value("/")  
  val ^ = Value("^")  
}

object Main {
  import scala.math
  def power(num: Int, n: Int) = {
    val nAbs = math.abs(n)
    @annotation.tailrec
    def loop(num_ :Int, n_ : Int, acc: Int): Int =
      if (0 == n_) 
        1
      else if(1 == n_)
        acc
      else 
        loop(num_ * acc, n_ - 1, num_ * num)

    loop(num, n, 1)
  }
    
  def main(args: Array[String]): Unit = {
    val n1 = new PureNumber(123)
    val n2 = new PureNumber(2)
    println {
     // n1 + n2
      power(2, 1)
    }
    
  }
}

