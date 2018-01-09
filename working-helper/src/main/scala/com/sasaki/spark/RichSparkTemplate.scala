package com.sasaki.spark

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2018-01-08 下午5:18:23
 * @Description 
 */
class RichSparkTemplate extends SparkHandler {
  
}

object RichSparkTemplate extends SparkHandler {
//    val conf = 
//      
//      new SparkConf().setMaster("local[*]")
//    .setAppName("XFiredParseProcess")
//    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//
//  val sc = new SparkContext(conf)
//  val spark = SparkSession.builder().enableHiveSupport().getOrCreate()
//   
//  import spark._
//  import spark.implicits._
//  
//  type M = LaunchMode.Value
//
//  implicit var _mode_ : M = _
//  implicit val _spark_ = spark
//  
//    def main(args: Array[String]): Unit = {
//        args match {
//      case Array() =>
//        println("> --------------------------------- Spark will start by local model. ---------------------------------------------")
////        System.setProperty("hadoop.home.dir", "H:\\hadoop-common-2.2.0-bin-master" /*调试启用临时目录*/ )
//        conf
//          .set("spark.executor.memory", "2G")
//          .set("spark.driver.memory", "1G")
//          .set("spark.driver.cores", "2")
//          .set("spark.driver.maxResultSize", "10G")
//          .set("spark.total.executor.cores", "2")
//        _mode_ = LaunchMode.DEVELOP; _data_dt = $u
//        FakeData.mockXFire("applicationvar", spark)
//      case Array(_1) =>
//        println("> --------------------------------- Spark will start by cluster model. ---------------------------------------------")
////        conf.setMaster(Master.yarn)
//        _data_dt = _1
//        _mode_ = LaunchMode.DEPLOY
//      case _ =>
//        println("Init args exception " + (args.foreach(o => print(o + " "))) + ", expect input: <data_dt[_ | yyyyMMdd]>")
//        throw new IllegalArgumentException(s"Init args exception $args")
//    }
  
  def main(args: Array[String]): Unit = {
    println {
    	buildConfWithoutMaster(Map("" -> "ss")).get("spark.app.name")
      
    }
  }
  
}