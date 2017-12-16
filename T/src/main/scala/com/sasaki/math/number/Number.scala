package com.sasaki.math.number

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

object Main {
    
  def main(args: Array[String]): Unit = {
    val n1 = new PureNumber(123)
    val n2 = new PureNumber(2)
    
    val n3 = CharNumber("3ab + 2b + 2b + a +d")
    val n4 = CharNumber("a")
    val n5 = CharNumber("a + 3c + 2b + 2b")
    
    println {
     // n1 + n2
//     n3 + n4
//      n3
//      n5
      
      "a + b+c - c".filter(p => p == '+' || p =='-')
    }
    
  }
}

