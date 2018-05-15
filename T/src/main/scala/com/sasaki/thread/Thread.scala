package com.sasaki.thread

package com.sasaki.thread

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 15, 2018 8:36:48 PM
 * @Description
 */
//runable没有返回值
trait _Runnable {
  def run(): Unit
}

//Callable有一个返回值
trait _Callable[V] {
  def call(): V
}