package com.sasaki.s2r.test

import scala.io.Source.fromInputStream

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import com.redislabs.provider.redis.RedisConfig
import com.redislabs.provider.redis.RedisEndpoint
import com.redislabs.provider.redis.rdd.Keys
import org.scalatest.junit.JUnitRunner
import com.redislabs.provider.redis.RedisFunctions

@RunWith(classOf[JUnitRunner])
class RedisClusterTest extends FunSuite with RedisFunctions with Keys with ENV with BeforeAndAfterAll with ShouldMatchers {
  
  override def beforeAll() {
    super.beforeAll()
    
    sc = new SparkContext(
        new SparkConf()
          .setMaster("local[1]")
          .setAppName(getClass.getName)
          .set("redis.host", "127.0.0.1")
          .set("redis.port", "7000")
    )
    content = fromInputStream(getClass.getClassLoader.getResourceAsStream("blog")).getLines.toArray.mkString("\n")
    val rddWordCount = sc.parallelize(content.split("\\W+")
        .filter(!_.isEmpty))
        .map((_, 1))
        .reduceByKey(_ + _)
        .map(x => (x._1, x._2.toString))
        // .foreach(println)
        
    val rddWords = sc.parallelize(content.split("\\W+")
        .filter(!_.isEmpty))
        // .foreach(println)
    
    redisConfig = new RedisConfig(new RedisEndpoint("127.0.0.1", 7000))
    redisConfig.hosts.foreach( node => {
      val conn = node.connect
//      conn.flushAll
      conn.flushDB
      conn.close
    })
    
    // 将RDD[(String, String)]数据装入redis
//    sc.toRedisKV(rddWordCount)(redisConfig)
    //
//      sc.toRedisZSET(rddWordCount, "all:words:cnt:sortedset" )(redisConfig)
//    sc.toRedisHASH(wcnts, "all:words:cnt:hash")(redisConfig)
//    sc.toRedisLIST(wds, "all:words:list" )(redisConfig)
//    sc.toRedisSET(wds, "all:words:set")(redisConfig)
  }
  
  test("t") {
    
  }
  
  override def afterAll() {
    sc.stop()
    System.clearProperty("spark.driver.port")
  }
}
