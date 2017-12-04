package com.sasaki.traitsample

import scala.collection.mutable.ArrayBuffer
/**
  * @Author Wei Liu
  * @Mail wei.liu@suanhua.org
  * @Timestamp 2017-12-4 下午11:49:02
  * @Description Scala Trait 用来实现可叠加的修改操作
  * Trait 能够修改类的方法，并且能够通过叠加这些操作（不同组合）修改类的方法。
  */
abstract class IntQueue {
  def get(): Int
  def put(x: Int)
}

class BasicIntQueue extends IntQueue {
  private val buf = new ArrayBuffer[Int]
  def get() = buf.remove(0)
  def put(x: Int) { buf += x }
}

/**
  * 1) 在添加元素时，添加元素的双倍，并且过滤掉负数
  * 2) 添加元素时，添加的为参数的递增值
  */

trait Doubling extends IntQueue {
  // 如果trait继承自某个类，则可以使用override关键字重写其方法
  // 在trait中，super是动态绑定的，它会根据对象的线性继承关系，指向其左边的class或者trait。
  abstract override def put(x: Int) = super/*动态绑定trait*/.put(2 * x) 
}

trait Incrementing extends IntQueue {
  abstract override def put(x: Int) = super.put(x + 1) 
}

trait Filtering extends IntQueue {
  abstract override def put(x: Int) = if (x >= 0) super.put(x)
}

object Main extends App {
  val queue = new BasicIntQueue
//  queue.put(1)
//  queue.put(2)
//  println(queue.get())
//  println(queue.get())
  
  /**
   * 新的队列类型，每次添加的都是参数的倍增
   */
  val queueDouble = new BasicIntQueue with Doubling 
  queueDouble.put(3)
  println(queueDouble.get())
  
  /**
   * 越后混合的 Trait 作用越大
   */
  val queueDoubleIncrement = new BasicIntQueue with Doubling with Incrementing
  queueDoubleIncrement.put(1)
  println(queueDoubleIncrement.get())
  
  val queueIncrementDouble = new BasicIntQueue with Incrementing with Doubling
  queueIncrementDouble.put(1)
  println(queueIncrementDouble.get())
  
  
}