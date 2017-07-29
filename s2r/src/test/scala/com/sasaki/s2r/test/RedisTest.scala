package com.sasaki.s2r.test

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import com.redislabs.provider.redis.RedisConfig
import com.redislabs.provider.redis.RedisEndpoint
import redis.clients.util.JedisClusterCRC16

@RunWith(classOf[JUnitRunner])
class RedisTest extends FunSuite {
  test("t") {
    println("ttt")
  }
  
    val redisStandaloneConfig = new RedisConfig(new RedisEndpoint("127.0.0.1", 6379, null))
//  val redisClusterConfig = new RedisConfig(new RedisEndpoint("127.0.0.1", 6379))

  test("getNodesBySlots") {
    assert(redisStandaloneConfig.getNodesBySlots(0, 16383).size == 1)
//    assert(redisClusterConfig.getNodesBySlots(0, 16383).size == 7)
  }
    
    test("getHost") {
    val key = "getHost"
    val slot = JedisClusterCRC16.getSlot(key)
    val standaloneHost = redisStandaloneConfig.getHost(key)
    assert(standaloneHost.startSlot <= slot && standaloneHost.endSlot >= slot)
//    val clusterHost = redisClusterConfig.getHost(key)
//    assert(clusterHost.startSlot <= slot && clusterHost.endSlot >= slot)
  }

  test("getNodes") {
    assert(redisStandaloneConfig.getNodes(new RedisEndpoint("127.0.0.1", 6379, null)).size == 1)
//    assert(redisClusterConfig.getNodes(new RedisEndpoint("127.0.0.1", 7379)).size == 7)
  }
}