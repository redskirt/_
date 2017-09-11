package com.sasaki.lambda

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017��9��11�� ����5:13:52
 * @Description
 */
class Test {
  def main(args: Array[String]): Unit = {
    val list = List(
      new Account(1, "n1", 10),
      new Account(2, "n2", 20),
      new Account(3, "n3", 40),
      new Account(4, "n4", 30))
    
    list.map(__ => (__.getId, __.setName(__.getName.toUpperCase())))
      .filter(_._2.getAge > 20 ).toMap foreach println
    
  }
  
  
}