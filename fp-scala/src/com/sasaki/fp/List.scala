package com.sasaki.fp

/**
 * 函数式数据结构
 * 单向链表
 */
sealed trait List[+T]

case object Nil extends List[Nothing] // 空List构造
case class Cons[+T](head: T, tail: List[T]) extends List[T] // 非空List构造

object List { // 伴生对象，包含List操作函数
//  def sum(list: List[Int]): Int = list match {
//    case Nil => 0
//    case Cons(__, __s) => __ + sum(__s)
//  }
  
//  def product(list: List[Double]): Double = list match {
//    case Nil => 1.0
//    case Cons(0.0, _) => 0.0
//    case Cons(__, __s) => __ * product(__s)
//  }
  
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
      case Cons(h, t) => if(f(h)) dropWhile(t, f) else Cons(h, dropWhile(t, f))
      case Nil => list
    }
  }
  
  // 参数分组，使得Scala能推导参数2与参数1类型相同，调用时可简写
  def dropWhile2[T](list: List[T])(f: T => Boolean): List[T] = {
    list match {
      case Cons(h, t) => if(f(h)) dropWhile2(t)(f) else Cons(h, dropWhile2(t)(f))
      case Nil => list
    }
  }
  
  /**
   * 利用共享数据特性将一个列表的所有元素加到另一个列表后面
   */
  def append[T](list: List[T], list_ : List[T]): List[T] = list match {
    case Nil => list_
    case Cons(h, t) => Cons(h, append(t, list_))
  }
  
  
  /**
   * 改进高阶函数的类型推导  
   * dropWhile -> dropWhile_
   * 记：此处程序仅对List元素有序自增时有效，且f 为 _ == ? 的判断无效
	 * 通过分析这里应该是仅对柯里化的演示
   */
  def dropWhile_[T](list: List[T])(f: T => Boolean): List[T] = {
    list match {
      case Cons(h, t) if f(h) => dropWhile_(t)(f)
      case _ => list
    }
  }
  
  
  /**
   * 基于list的递归并泛化为高阶函数
   */
  def sum(list: List[Int]): Int = list match {
    case Nil => 0
    case Cons(__, __s) => __ + sum(__s)
  }
  
  def product(list: List[Double]): Double = list match {
    case Nil => 1.0
    case Cons(__, __s) => __ * product(__s)
  }
  
  /**
   * 右折叠的简单运用
   * 将sum和product进行泛化
   */
  def foldRight[A, B](list: List[A], b : B)(f: (A, B) => B): B = list match {
    case Nil => b
    case Cons(h, t) => f(h, foldRight(list, b)(f))
  }
  
  def sum2(list: List[Int]) = foldRight(list, 0)((x, y) => x + y)
  
  def product2(list: List[Double]) = foldRight(list, 1.0)(_ * _) // _ * _ 即 (x, y) => x * y 的简写
  
}

object Main {
  def main(args: Array[String]): Unit = {
    import com.sasaki.fp.List._

    /**
     * P.30/练习3.2 测试
     * 实现tail函数，删除一个List的第一个元素。
     */
    // List.tail(List())
    println(tail(List("a", "b", "c")))
    // Cons(b,Cons(c,Nil))

    println(setHead(List(), "head"))
    // Cons(head,Nil)
    
    println(setHead(List("head", "tail"), "head_"))
    // Cons(head_,Cons(tail,Nil))
    
    println(setHead(List(1, 2, 3), 10))
    // Cons(10,Cons(2,Cons(3,Nil)))

    println(drop(List(1, 2, 3, 4), 3))
    // Cons(1,Cons(2,Cons(3,Cons(4,Nil))))

    println(dropWhile(List(1, 2, 3), (x: Int) => x == 2))
    // Cons(1,Cons(3,Nil))
    
    // 参数分组后Scala推导参数类型
    println(dropWhile2(List(1, 2, 3))(_ <= 2))
    // Cons(3,Nil)

    // 改进高阶函数类型推导
    println(dropWhile_(List(1, 2, 3))(x => x == 2))
    // Cons(1,Cons(2,Cons(3,Nil)))
    
    println(dropWhile_(List(1, 2, 3))(_ == 2))
    // Cons(1,Cons(2,Cons(3,Nil)))

    println(dropWhile_(List(1, 2, 4))(_ <= 2))
    // Cons(4,Nil)

    println(dropWhile_(List(1, 2, 3, 4, 9, 4, 8, 10))(_ < 8))
    // Cons(9,Cons(4,Cons(8,Cons(10,Nil))))
    
  }

}
