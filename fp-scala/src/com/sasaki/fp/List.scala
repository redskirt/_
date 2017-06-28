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
  
  /**
   * P.30/练习3.2 
   * 实现tail函数，删除一个List的第一个元素。
   * !!!
   */
  def tail[T](list: List[T]): List[T] = list match {
    case Nil => sys.error("empty list!")
    case Cons(_, t) => t
  }
 
  /**
   * P.30/练习3.3
   * 实现函数setHead用一个不同的值替代列表中的第一个元素。
   * !!!
   */
  def setHead[T](list: List[T], t: T): List[T] = list match {
    case Nil => Cons(t, Nil)
    case Cons(_, tail) => Cons(t, tail)
  }
  
  /**
   * P.30/练习3.4
   * 把tail泛化为drop函数，用于从列表中删除前n个元素。
   * ???
   */
  def drop[T](list: List[T], n: Int): List[T] = {
    if(n <= 0) list
    else list match {
      case Nil => Nil
      case Cons(_, t) => drop(list, n - 1)
    }
  }
  
  /**
   * P.30/练习3.5
   * 实现dropWhile函数，删除列表中前缀全部符合判定的元素。
   * !!!
   */
  def dropWhile[T](list: List[T], f: T => Boolean): List[T] = {
    list match {
      case Nil => list
      case Cons(h, t) => if(f(h)) dropWhile(t, f) else Cons(h, dropWhile(t, f))
    }
  }
  
  /**
   * 利用共享数据特性将一个列表的所有元素加到另一个列表后面
   */
  def append[T](list: List[T], list_ : List[T]): List[T] = list match {
    case Nil => list_
    case Cons(h, t) => Cons(h, append(t, list_))
  }
  
  
    
}
