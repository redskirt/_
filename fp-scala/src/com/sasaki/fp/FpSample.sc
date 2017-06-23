package com.sasaki.fp

object FpSample {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  // 准备
  class Coffee {
    val price: Double = 0
  }

  class CreditCard {
    def charge(cost: Double) = ???
  }

  // P3. 售咖啡案例
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

}