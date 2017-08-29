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

object SparkTemplate extends Object with T {
  conf
    .setAppName(Util.getSimpleName(this))
    .setMaster("local[1]")
    .set("_key_", "_value_")

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(conf)
    
    invokeHandler(sc) { () =>
//      sc.textFile("""file:///H:\迅雷下载\spark-2.1.1\graphx\pom.xml""", 1).flatMap(_.split(' ')).map((_, 1)).reduceByKey(_ + _) foreach println
    val fs = List(
        FrdAppReqLog(1, 1, 2, 2),
        FrdAppReqLog(1, 2, 21, 12),
        FrdAppReqLog(1, 3, 1, 32),
        FrdAppReqLog(1, 2, 22, 32),
        FrdAppReqLog(1, 6, 3, 24)
    )
    
    val rdd = sc.parallelize(fs, 1)
    val f: (FrdAppReqLog, FrdAppReqLog) => FrdAppReqLog = 
      (_o: FrdAppReqLog, o_ : FrdAppReqLog) => FrdAppReqLog(
          _o.count + o_.count,
          _o.total_costTime + o_.total_costTime, 
          _o.Validate_costTime + o_.Validate_costTime, 
          _o.Clean_costTime + o_.Clean_costTime)
    rdd.reduce(f)
    }
  }
}


abstract class B[-E] protected() { 
  var _id: Int = _ 
  def _Id(id: Int): B[E] = { this._id_=(id); this } 
  def avg(){}
}

case class FrdAppReqLog (
   count: Int,
   total_costTime: Int,
   Validate_costTime: Int,
   Clean_costTime: Int
//  ExtendCheck_costTime: Int,
//  VarsNetwork_costTime: Int,
//  SHRuleEngine_costTime: Int,
//  Resp_costTime: Int
) extends B[FrdAppReqLog] { 
  
  
}
