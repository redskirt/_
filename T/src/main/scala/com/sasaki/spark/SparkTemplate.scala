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
  val conf = _conf_(Util.getSimpleName(this), List(("_key_" -> "_value_")))

  def main(args: Array[String]): Unit = {
//    val sc = new SparkContext(conf)
    
//    invokeHandler(sc) { () =>
//      sc.textFile("""file:///H:\迅雷下载\spark-2.1.1\graphx\pom.xml""", 1).flatMap(_.split(' ')).map((_, 1)).reduceByKey(_ + _) foreach println
//    val fs = List(
//        FrdAppReqLog(1, 1, 2, 2),
//        FrdAppReqLog(1, 2, 21, 12),
//        FrdAppReqLog(1, 3, 1, 32),
//        FrdAppReqLog(1, 2, 22, 32),
//        FrdAppReqLog(1, 6, 3, 24)
//    )
//    
//    val rdd = sc.parallelize(fs, 1)
//    val f: (FrdAppReqLog, FrdAppReqLog) => FrdAppReqLog = 
//      (_o: FrdAppReqLog, o_ : FrdAppReqLog) => FrdAppReqLog(
//          _o.count + o_.count,
//          _o.total_costTime + o_.total_costTime, 
//          _o.Validate_costTime + o_.Validate_costTime, 
//          _o.Clean_costTime + o_.Clean_costTime)
//    rdd.reduce(f)
      
    val logs = List(
      Aggregator(Seq(1, 1, 3, 4)),    
      Aggregator(Seq(15, 1, 3, 4)),    
      Aggregator(Seq(1, 11, 3, 4)),    
      Aggregator(Seq(1, 12, 3, 4)),    
      Aggregator(Seq(1, 1, 3, 4))    
    )
//    val aggr = sc.parallelize(logs, 1).reduce(_.combine(_)((_o: Double, o_ :Double) => _o + o_))
    
//    println(aggr)
    
    
    
//    implicit val add = (_o: Int, o_ : Int) => _o + o_
//    println(Aggregator(Seq(1, 1, 3, 4), 1).combine(Aggregator(Seq(11, 12, 3, 4), 1)).combine(Aggregator(Seq(1, 1, 3, 4), 1)))
//      println(Aggregator(Seq(1, 1, 3, 4), 3).within((_o, o_) => _o / o_))
      
//    }
  }
}

case class Aggregator(factor: Seq[Double], c: Int = 1) {
  require(factor.length != 0, "factor have to nonEmpty! " + factor)
  
  def within(f_x: (Double, Double) => Double) = {
    val _factor_ = this.factor.map(f_x(_, this.c).formatted("%.2f").toDouble)
    Aggregator(_factor_, c)
  }
  
  def combine(that : Aggregator)(implicit f_x: (Double, Double) => Double) = {
    val _factor_ = for { 
      i <- 0 until that.factor.length
      if(this.factor.length == that.factor.length)
    } yield  f_x(factor(i), that.factor(i))
    Aggregator(_factor_, f_x(this.c, that.c).toInt)
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
