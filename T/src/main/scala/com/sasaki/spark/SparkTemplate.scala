package com.sasaki.spark

import org.apache.spark.SparkConf
import com.sasaki.o.Util
import org.apache.spark.SparkContext

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08*28 下午2:59:24
 * @Description
 */
class SparkTemplate {

}

object SparkTemplate {
  
  def processHandler(sc: SparkContext)(f: Array[String] => Unit) {
    try {
      
    } finally
      sc.stop()
  }

  val conf = new SparkConf()
    .setAppName(Util.getSimpleName(this))
    .setMaster("")
    .set("_key_", "_value_")

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(conf)

    
    
    
    
    val data = Array(('a', 1), ('a', 2), ('b', 3), ('a', 4), ('a', 15))
    val distData = sc.parallelize(data).map(x => (1, x._2))
    val add = (x: (Int, Int), y: (Int, Int)) => { (x._1 + y._1, x._2 + y._2) }
    val ret = distData.reduce(add)
    print(ret._2 / ret._1)

  }
}
