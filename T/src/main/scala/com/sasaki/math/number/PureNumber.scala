package com.sasaki.math.number

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