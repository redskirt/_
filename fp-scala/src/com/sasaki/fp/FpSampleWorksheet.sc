package com.sasaki.fp

object FpSample {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  // 对x是“引用透明”特性的说明
  val x = "Hello, World"                          //> x  : String = Hello, World

  val r1 = x.reverse                              //> r1  : String = dlroW ,olleH
  val r2 = x.reverse                              //> r2  : String = dlroW ,olleH

  val r1_ = "Hello, World".reverse                //> r1_  : String = dlroW ,olleH
  val r2_ = "Hello, World".reverse                //> r2_  : String = dlroW ,olleH

  // 相比x，y则不是“引用透明”的，使用与y等价的表达式替换计算得出了不一致的结果
  val y = new StringBuilder("Hello")              //> y  : StringBuilder = Hello

  val y_ = y.append(", World")                    //> y_  : StringBuilder = Hello, World

  val r3 = y_                                     //> r3  : StringBuilder = Hello, World
  val r4 = y_                                     //> r4  : StringBuilder = Hello, World

  // 把y_替换成其引用的表达式，则不再相等
  val r3_ = y.append(", World")                   //> r3_  : StringBuilder = Hello, World, World
  val r4_ = y.append(", World")                   //> r4_  : StringBuilder = Hello, World, World, World

  // 声明函数字面量判断两个数相等
  val equal = (x: Int, y: Int) => x == y          //> equal  : (Int, Int) => Boolean = <function2>
  equal(1, 3)                                     //> res0: Boolean = false

  // 字面量(x, y) => x < y实际上是一段创建函数对象的语法糖
  val lessThan = new Function2[Int, Int, Boolean] {
    def apply(x: Int, y: Int) = x < y
  }                                               //> lessThan  : (Int, Int) => Boolean = <function2>

  lessThan.apply(1, 4)                            //> res1: Boolean = true

  /*
		* 部分应用 partiall application
		* 表示函数被应用的参数不是它所需要的完整的参数
		*/
  def partiall[A, B, C](a: A, f: (A, B) => C): B => C = ???
                                                  //> partiall: [A, B, C](a: A, f: (A, B) => C)B => C
  def partiall2[A, B, C](a: A, f: (A, B) => C): B => C = (b: B) => ???
                                                  //> partiall2: [A, B, C](a: A, f: (A, B) => C)B => C
  def partiall3[A, B, C](a: A, f: (A, B) => C): B => C = (b: B) => f(a, b)
                                                  //> partiall3: [A, B, C](a: A, f: (A, B) => C)B => C

  /*
	  * P.22/练习2.3 柯里化
	  * 把带有两个参数的函数f转换为只有一个参数的部分应用函数f。
	  */
  def curry[A, B, C](f: (A, B) => C): A => (B => C) = a => b => f(a, b)
                                                  //> curry: [A, B, C](f: (A, B) => C)A => (B => C)
  // “=>”是右结合的，a => b => f(a, b)即 a => (b => f(a, b))

  /*
	  * P.22/练习2.4 反柯里化
	  */
  def uncurry[A, B, C](f: A => B => C): (A, B) => C = ???
                                                  //> uncurry: [A, B, C](f: A => (B => C))(A, B) => C

  /*
		* P.22/练习2.5
		* 实现一个高阶函数,可以组合两个函数为一个函数。
		*/
  def compose[A, B, C](f: B => C, g: A => B): A => C = (a: A) => f(g(a))
                                                  //> compose: [A, B, C](f: B => C, g: A => B)A => C

  /**
   * 测试List.scala 模式匹配
   */
  // 匹配任意表达式，结果为4
  List(1, 2, 4, 5) match { case _ => 4 }          //> res2: Int = 4
  // 匹配Head，使用了构造器模式结合变量模式
  List(1, 2, 4, 5) match { case Cons(h, _) => h } //> res3: Int = 1
  // 匹配Tail
  List(1, 2, 4, 5) match { case Cons(_, t) => t } //> res4: com.sasaki.fp.List[Int] = Cons(2,Cons(4,Cons(5,Nil)))
  // 匹配Nil，没有表达式与目标匹配，报错
  // List(1, 2, 4, 5) match { case Nil => -1  }

  /*
	* P.28/练习3.1
	* 匹配表达式的结果是？
	*/
  val o = List(1, 2, 3, 4, 5) match {
    // case Cons(x, Cons(2, Cons(4, _))) => x // ex
    // case Nil => 42 //ex
    // case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y // 3
    // case Cons(h, t) => h + List.sum(t) // 15
    case _ => 101 // 101
  }                                               //> o  : Int = 101

	// println(List.length(List("a", "b", "c")))

}