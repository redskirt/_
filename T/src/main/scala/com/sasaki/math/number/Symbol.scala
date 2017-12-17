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
  
  val symbols = Array($_+, $_-, $_*)
  
  /**
   * 判断字符是否为任意操作符类型  
   */
  def isOperator(o: Char) = 
    o == $_+ || 
    o == $_- || 
    o == $_*
}