package com.sasaki.math.number

object Symbol extends Enumeration {
  type Symbol = Value
  val + = Value("+")  
  val - = Value("-")  
  val * = Value("*")  
  val / = Value("/")  
  val ^ = Value("^")  
  
  val $_+ = '+'
  val $_- = '-'
  val $_* = '*'
}