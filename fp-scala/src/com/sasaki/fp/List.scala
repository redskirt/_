package com.sasaki.fp

/**
 * 函数式数据结构
 * 单向链表
 */
sealed trait List[+T]

case object Nil extends List[Nothing] // 空List构造
case class Cons[+T](head: T, tail: List[T]) extends List[T] // 非空List构造

object List { // 伴生对象，包含List操作函数
  def sum(list: List[Int]): Int = list match {
    case Nil => 0
    case Cons(__, __s) => __ + sum(__s)
  }
  
  def product(list: List[Double]): Double = list match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(__, __s) => __ * product(__s)
  }
  
  def apply[T](list: T*): List[T] = if(list.isEmpty) Nil else Cons(list.head, apply(list.tail: _*))
  
  // 构造List方式
  val list1: List[Double] = Nil
  val list2: List[Int] = Cons(1, Nil)
  val list3: List[String] = Cons("a", Cons("b", Nil))
 
  
      // 模式匹配
//  List(1, 2, 4, 5) match { case _ => 4  }
//  List(1, 2, 4, 5) match { case Nil => 0  }
    
}
