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
  case class Charge(coffee: Coffee, amount: Double) {
    def combine(charge_ : Charge) = {
      if (coffee == charge_.coffee)
        Charge(coffee, amount + charge_.amount)
      else
        throw new Exception("Can't combint charges to different cards.")
    }
  }

  // 把费用的创建过程与执行分离
  def buyCoffee_(card: CreditCard): (Coffee, Charge) = {
    val cup = new Coffee()
    (cup, Charge(cup, cup.price))
  }

  // 购买多杯咖啡的情况
  def buyCoffees(card: CreditCard, n: Int): (List[Coffee], Charge) = {
    // List.fill(n)(x) 创建一个对x复制n份的列表
    val purchases: List[(Coffee, Charge)] = List.fill(n)(buyCoffee_(card))
    val (coffees, charges) = purchases.unzip
    (coffees, charges.reduce((c, c_) => c.combine(c_)))
  }

  // 把同一张信用卡的费用合并为一个List[Charge]
  def coalesce(charges: List[Charge]): List[Charge] = charges.groupBy(_.coffee).values.map(_.reduce(_ combine _)).toList
}

// -------------------- P.11 Scala程序  ------------------------
object Module {
  // 求绝对值
  def abs(n: Int): Int = if (n < 0) -n else n

  private def formatAbs(n: Int) = "The absolute value of %d is %d".format(n, abs(n))

  def main(args: Array[String]): Unit = {
    //    println(formatAbs(-5))
    //    println(factorial(366))
    //    println(tfib(6))
  }

  // P.16 阶乘 
  def factorial(n: Int): Int = {
    @annotation.tailrec // 尾递归优化
    def loop(n: Int, acc: Int): Int = if (n <= 0) acc else loop(n - 1, n * acc)

    loop(n, 1)
  }

  // ------------------------ P.17/练习2.1 斐波那契数  --------------------------
  // 递归
  def fib(n: Int): Int = if (n <= 2) n - 1 else fib(n - 1) + fib(n - 2)

  // 尾递归
  //  def tfib(n: Int): Int = {
  //    def local(n: Int, acc : Int, acc_ : Int): Int = if(n <= 2) n - 1 else local(n - 1, acc_, acc + acc_)
  //    local(n, 0, 1)
  //  }

}



