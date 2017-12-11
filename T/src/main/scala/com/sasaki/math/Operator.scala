package com.sasaki.math

/**
 * @Author Sasaki
 */

//一元操作与二元操作

abstract class Number[T <: Number[_]] {
  def +(n: T): T
//  def -(n: T): T
//  def *(n: T): T
//  def %(n: T): T
//  def ^ : T
}

class PureNumber(val n: Int) extends Number[PureNumber] {
  override def +(n: PureNumber): PureNumber = new PureNumber(this.n + n.n)
//  override def -(n: Number): Number = this - n
//  override def *(n: Number): Number = this * n
//  override def %(n: Number): Number = this % n
//  override def ^ : Number = ???
  
//  implicit def Int2PureNumber(value: Int) = new PureNumber(value)
}

//class CharNumber(s: String) extends Number

object Symbol extends Enumeration {
  type Symbol = Value
  val + = Value("+")  
  val - = Value("-")  
  val * = Value("*")  
  val % = Value("%")  
  val ^ = Value("^")  
}

