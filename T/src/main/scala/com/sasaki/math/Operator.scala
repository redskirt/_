package com.sasaki.math

import scala.math
import independent._

/**
 * @Author Sasaki
 */

sealed trait Number[T <: Number[_]] {
  
  def +(n: T): T
  
  def -(n: T): T
  
  def *(n: T): T
  
  def /(n: T): T
  
  def ^(n: Int): T
  
  protected def power(n: T, i: Int): T
}

abstract class AbstractNumber[T <: AbstractNumber[_]] extends Number[T] 

class PureNumber(val $v: Int) extends AbstractNumber[PureNumber] {
  type P = PureNumber
  override def +(o: P): P = PureNumber($v + o.$v)
  override def -(o: P): P = PureNumber($v - o.$v)
  override def *(o: P): P = PureNumber($v * o.$v)
  override def /(o: P): P = PureNumber($v / o.$v)
  override def ^(i: Int): P = power(PureNumber($v), i)

  protected override def power(num: P, n: Int): P = {
    @annotation.tailrec
    def loop(num_ :Int, n_ : Int, acc: Int): Int =
      if (0 == n_)
        1
      else if (1 == n_)
        acc
      else
        loop(num_ * acc, n_ - 1, num_ * num.$v)

    val v = loop(num.$v, math.abs(n), 1)
    PureNumber({ if (n >= 0) v else 1 / v })
  }

  //  implicit def Int2PureNumber(value: Int) = new PureNumber(value)
  override def toString = $v.toString()
}

object PureNumber {
  def apply($v: Int) = new PureNumber($v)
}

class CharNumber(val $v: String) extends AbstractNumber[CharNumber] {
  
  import regex._
  import Symbol._
  
  type C = CharNumber
  
    //      $v.split($_+).map { o =>
//        extractNumbers(erase(o, $e)/*each $v*/).reduce(_ * _)
//      }.reduce(_ + _)
  
  def parse$V: Map[Int, String] = 
        if($v.contains($_+)) {
          // 3a + 2b + 2b -> (3, a) (4, b)
         $v.split($_+).map { o =>
            (extractNonNumbers(o).reduce(_ + _), extractNumbers(o).reduce(_ * _))
          }.groupBy(o => o._1).map { case (k, ks_vs)=>
            (ks_vs.map(_._2).reduce(_ + _) -> k)
          }
        }
      else // 3a2b -> 6 
      Map(extractNumbers($v).reduce(_ * _) -> extractNonNumbers($v).reduce(_ + _))
  
  protected val coefficient = 
    if($v.contains($_+)) // 3a + 2b + 2b -> 0
      0
//      $v.split($_+).map { o =>
//        extractNumbers(erase(o, $e)/*each $v*/).reduce(_ * _)
//      }.reduce(_ + _)
    else // 3a2b -> 6
      extractNumbers($v).reduce(_ * _)
      
  protected val item = extractNonNumbers($v).distinct.reduce(_ + _) 
  
  // 2ab + 3a
  override def +(o: C): C = {
    val coefficient_ = o.coefficient
    val item_ = o.item
    println(coefficient_ + " " + item_ + " "+ coefficient)
    CharNumber({
      if(item == item_) // 2a and 3a -> 5a
        s"${coefficient + coefficient_}$item"
      else // 2a and 3b -> 2a + 3b
        s"${coefficient}$item + $coefficient_$item_"
    })
  }
    
  override def -(n: C): C = ???
  override def *(n: C): C = ???
  override def /(n: C): C = ???
  override def ^(i: Int): C = ???
  protected override def power(num: C, n: Int): C = ???
  
  override def toString = parse$V.mkString(" + ") 
}

object CharNumber {
  def apply($s: String) = new CharNumber($s)
}

//class CharNumber(s: String) extends Number
//object ImplicitAdapter {
//  implicit final class Int2StringAdd(private val self: Int) extends AnyVal {
//    def +(other: String): String = String.valueOf(self) + other
//  }
//}

object Symbol extends Enumeration {
  type Symbol = Value
  val + = Value("+")  
  val - = Value("-")  
  val * = Value("*")  
  val / = Value("/")  
  val ^ = Value("^")  
  
  val $_+ = "\\+"
}

object Main {
  import scala.math
  def power(num: Int, n: Int) = {
    val nAbs = math.abs(n)
    @annotation.tailrec
    def loop(num_ :Int, n_ : Int, acc: Int): Int =
      if (0 == n_) 
        1
      else if(1 == n_)
        acc
      else 
        loop(num_ * acc, n_ - 1, num_ * num)

    loop(num, n, 1)
  }
    
  def main(args: Array[String]): Unit = {
    val n1 = new PureNumber(123)
    val n2 = new PureNumber(2)
    
    val n3 = CharNumber("3a + 2b + 2b")
    val n4 = CharNumber("3a")
    println {
     // n1 + n2
//     n3 + n4
      n3
    }
    
  }
}

