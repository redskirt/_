package com.sasaki.thread

package com.sasaki.thread

import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask
import java.util.concurrent.Executors
import java.util.concurrent.Callable

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 15, 2018 8:34:37 PM
 * @Description Callable执行完有返回值
 */
object ThreadCallableSample {
  def main(args: Array[String]) {
    val threadPool:ExecutorService=Executors.newFixedThreadPool(3)
    try {
      val future=new FutureTask[String](new Callable[String] {
        override def call(): String = {
          Thread.sleep(100)
          return "im result"
        }
      })
      threadPool.execute(future)
      println(future.get())
    }finally {
      threadPool.shutdown()
    }
  }
}