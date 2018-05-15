package com.sasaki.wp.sample

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 15, 2018 8:51:57 PM
 * @Description
 */
object ThreadModel {

  def main(args: Array[String]) {
    val threadPool: ExecutorService = Executors.newFixedThreadPool(5)
    try {
      for (i <- 1 to 500) {
        
        threadPool.execute(new Process(i))
      }
    } finally {
      threadPool.shutdown()
    }
  }

  class Process(id: Int) extends Runnable {
    override def run() {
      println(id)
      Thread.sleep(2000)
    }
  }

}