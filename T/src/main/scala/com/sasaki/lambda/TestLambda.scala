package com.sasaki.lambda

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-05 下午2:32:48
 * @Description
 */
class TestLambda {

}

trait Anyt {
  def age(): Int
}

object TestLambda {
  def getAge(any: Anyt) = any.age()

  def main(args: Array[String]): Unit = {
    val any = new Anyt { override def age() = 20 }
//    println(getAge(any))
//    println(getAge(new Anyt { override def age() = 20 }))
//    println(getAge(() => 20))
    
    println("acb".capitalize)
  }
}

