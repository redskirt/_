package com.sasaki.fp

import scala.annotation.migration

// -----------------------  P.3 售咖啡案例  -----------------------
class Coffee {
  val price: Double = 0
}

class CreditCard {
  def charge(cost: Double) = ???
}

class Cafe {
  def buyCoffee(card: CreditCard): Coffee = {
    val cup = new Coffee()
    card.charge(cup.price) // 方法副作用，调用额外消费接口
    cup
  }

  // 改造1，添加支付对象
  class Payments {
    def charge(card: CreditCard, cost: Double) = ???
  }

  def buyCoffee(card: CreditCard, pay: Payments) = {
    val cup = new Coffee()
    pay.charge(card, cup.price) // 模拟使用实现支付接口的对象执行支付
    cup
  }

  // 改造2， 函数式
  case class Charge(card: CreditCard, amount: Double) {
    // 根据card合并计算金额，即同一张信用卡消费的总金额
    def combine(charge_ : Charge) = {
      if (card == charge_.card)
        Charge(card, amount + charge_.amount)
      else
        throw new Exception("Can't combine charges to different cards.")
    }
  }

  // 把费用的创建过程与执行分离
  def buyCoffee_(card: CreditCard): (Coffee, Charge) = {
    val cup = new Coffee()
    (cup, Charge(card, cup.price))
  }

  // 购买多杯咖啡的情况
  def buyCoffees(card: CreditCard, n: Int): (List[Coffee], Charge) = {
    // List.fill(n)(x) 创建一个对x复制n份的列表
    val purchases: List[(Coffee, Charge)] = List.fill(n)(buyCoffee_(card))
    val (coffees, charges) = purchases.unzip
    (coffees, charges.reduce((c, c_) => c.combine(c_)))
  }

  // 把同一张信用卡的费用合并为一个List[Charge]
  def coalesce(charges: List[Charge]): List[Charge] = charges.groupBy(_.card).values.map(_.reduce(_ combine _)).toList
}

// --------------------  P.11 Scala程序   ------------------------
object Module {
  // 求绝对值
  def abs(n: Int): Int = if (n < 0) -n else n

  // P.16 阶乘 
  def factorial(n: Int): Int = {
    @annotation.tailrec // 尾递归优化
    def loop(n: Int, acc: Int): Int = if (n <= 0) acc else loop(n - 1, n * acc)

    loop(n, 1)
  }

  // ------------------------ P.17/练习2.1 斐波那契数  --------------------------
  // 递归实现
  def fib(n: Int): Int = if (n <= 2) n - 1 else fib(n - 1) + fib(n - 2)

  // 尾递归实现
  def tfib(n: Int): Int = {
    @annotation.tailrec
    def loop(n: Int, acc: Int, acc_ : Int): Int =
      if (n <= 0) -1
      else if (n == 1) acc
      else if (n == 2) acc_
      else loop(n - 1, acc_, acc + acc_)

    loop(n, 0, 1)
  }

  // ----------------------------  P.17 高阶函数  ------------------------------
  private def formatAbs(x: Int) = "The absolute value of %d is %d".format(x, abs(x))

  private def formatFactorial(n: Int) = "The factorial of %d is %d".format(n, factorial(n))

  // 将formatAbs和formatFactorial泛化
  def formapResult(name: String, n: Int, f: Int => Int /*接收函数参数*/ ) = "Thr %s of %d is %d".format(name, n, f(n))

  // 在数组中查找字符串的单态函数
  def findFirst(array: Array[String], key: String): Int = {
    @annotation.tailrec
    def loop(n: Int): Int = if (n >= array.length) -1 else if (array(n) == key) n else loop(n + 1)

    loop(0)
  }

  // 在数组中查找字符串的多态函数
  def findFirst[T](array: Array[T], p: T => Boolean): Int = {
    @annotation.tailrec
    def loop(n: Int): Int = if (n >= array.length) -1 else if (p(array(n)) /*使用函数匹配当前元素*/ ) n else loop(n + 1)

    loop(0)
  }

  // -------  P.20/练习2.2 实现isSorted方法，检测Array[A]是否按照给定的比较函数排序  --------
  def isSorted[A](array: Array[A], ordered: (A, A) => Boolean): Boolean = {
    def loop(n: Int): Boolean = {
      if (array.length <= n + 1)
        true
      else if (ordered(array(n), array(n + 1))) 
        loop(n + 1) 
      else 
        false
    }

    loop(0)
  }

  def main(args: Array[String]): Unit = {
    //    println(formatAbs(-5))
    //    println(factorial(366))
    //    println(fib(50))
    //    println(tfib(45))
    //    def ordered(x: Int, x_ : Int) = x < x_
    //    println(isSorted(Array(9, 1, 3, 4), ordered))
    //    println(isSorted(Array(1, 2, 3, 0), (x: Int, x_ : Int) => x < x_))
  }

}







