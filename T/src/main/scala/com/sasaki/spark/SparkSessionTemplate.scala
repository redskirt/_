package com.sasaki.spark

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-18 上午10:00:02
 * @Description 
 */
class SparkSessionTemplate {
  
}

object SparkSessionTemplate extends SparkHandler {
    val conf = _conf_(independent.getSimpleName(this), List(("_key_" -> "_value_")))
    
    def main(args: Array[String]): Unit = {
      
    }
    
}