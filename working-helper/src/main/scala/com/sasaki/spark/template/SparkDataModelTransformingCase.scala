package com.sasaki.spark.template

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2018-01-11 18:10:03
 * @Description Spark SQL, DataFrames, Datasets 相互转换示例
 * 
 * @see http://spark.apache.org/docs/latest/sql-programming-guide.html#datasets-and-dataframes
 */
object SparkDataModelTransformingCase extends com.sasaki.spark.SparkHandler {

	implicit val spark = buildLocalSparkSession(false)

  import logger._ 
  import spark.implicits._

  def main(args: Array[String]): Unit = {

    invokeSessionHandler { () =>
      /**
       * Named alias:
       * DataFrame		DF
       * Dataset			DS
       * RDD					RDD
       */
      
      info(">--------------------------------------- Initialization -------------------------------------------")
      
      // 常规初始化RDD、DF、DS方式，包括但不限于以下列举形式
      
      /**
       * Create RDD
       */
      @deprecated("Spark-1.* Version.")
      val rdd = spark.sparkContext.parallelize(Seq(Person("Andy", 32)))
      
      /**
       * Create DF
       */
      val df = spark.read.json("src/main/resources/sample/people.json")
      
      /**
       * Create DS
       */
      val ds = Seq(Person("Andy", 32)).toDS()
      
      info(">--------------------------------------- Initialization -------------------------------------------")

      // RDD、DF、DS之间互转方式，包括但不限于以下列举形式
      
      info(">--------------------------------------- DF -------------------------------------------")
      /**
       * DF
       */
      val df1 = spark.read.json("src/main/resources/sample/people.json")
      df1.show()
      //      +----+-------+
      //      | age|   name|
      //      +----+-------+
      //      |null|Michael|
      //      |  30|   Andy|
      //      |  19| Justin|
      //      +----+-------+

      /**
       * DF -> DF
       */
      val df2 = df1.select("name").where("age is not null").show()
      //      +------+
      //      |  name|
      //      +------+
      //      |  Andy|
      //      |Justin|
      //      +------+

      /**
       * DF -> RDD
       */
      val rdd1 = df1.rdd
      rdd1 foreach println
      //      [null,Michael]
      //      [30,Andy]
      //      [19,Justin]

      /**
       * DF -> DS[String], Json string type
       */
      val ds1 = df1.toJSON
      ds1.show(false)
      //      +--------------------------+
      //      |value                     |
      //      +--------------------------+
      //      |{"name":"Michael"}        |
      //      |{"age":30,"name":"Andy"}  |
      //      |{"age":19,"name":"Justin"}|
      //      +--------------------------+

      /**
       * DF -> DS[Person], Person class type
       */
      val ds2 = df1.as[Person]
      ds2.show(false)
      //      +----+-------+
      //      |age |name   |
      //      +----+-------+
      //      |null|Michael|
      //      |30  |Andy   |
      //      |19  |Justin |
      //      +----+-------+
      info(">--------------------------------------- DF -------------------------------------------")

      info(">--------------------------------------- DS -------------------------------------------")
      /**
       * Primitive DS
       * DS[Int]
       */
      val ds3 = Seq(1, 2, 3).toDS()
      ds3.show()
      //      +-----+
      //      |value|
      //      +-----+
      //      |    1|
      //      |    2|
      //      |    3|
      //      +-----+

      /**
       * DS -> DS
       * DS[String], Json string type
       */
      val ds4 = ds3.toJSON
      ds4.show()
      //      +-----------+
      //      |      value|
      //      +-----------+
      //      |{"value":1}|
      //      |{"value":2}|
      //      |{"value":3}|
      //      +-----------+

      /**
       * DS -> DF
       * @see ds3
       */
      val df3 = ds3.toDF()
      df3.show()

      /**
       * DS -> RDD
       * RDD[Int]
       */
      val rdd2 = ds3.rdd
      rdd2 foreach println
      //      1
      //      3
      //      2

      /**
       * Case class DS
       * DS[Person]
       */
      val ds5 = Seq(Person("Andy", 32)).toDS()
      ds5.show()
      //      +----+---+
      //      |name|age|
      //      +----+---+
      //      |Andy| 32|
      //      +----+---+

      /**
       * DF -> DS[String], Json string type
       * @see ds1
       */
      val ds6 = ds5.toJSON

      /**
       * DS -> DF
       * @see ds5
       */
      val df4 = ds5.toDF()
      df4.show()

      /**
       * DS -> RDD
       */
      val rdd3 = ds5.rdd
      rdd3 foreach println
      //      Person(Andy,32)
      
      info(">--------------------------------------- DS -------------------------------------------")
      
      info(">--------------------------------------- RDD -------------------------------------------")
      
      /**
       * RDD[Person]
       * @see rdd3
       */
      @deprecated("Spark-1.* Version.")
      val rdd4 = spark.sparkContext.parallelize(Seq(Person("Andy", 32)))
      rdd4 foreach println
      
      /**
       * RDD -> Seq[Person]
       * @see rdd3
       */
      rdd4.collect foreach println
      
      /**
       * RDD -> DF
       * @see ds5
       */
      val df5 = rdd4.toDF()
      df5.show()
      
      /**
       * RDD -> DS[Person]
       * @see ds5
       */
      val ds7 = rdd4.toDS()
      ds7.show()
      
      info(">--------------------------------------- RDD -------------------------------------------")
    }
  }
}

case class Person(name: String, age: Long)