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

