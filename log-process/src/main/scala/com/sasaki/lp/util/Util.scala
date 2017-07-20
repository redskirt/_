package com.sasaki.lp.util


/**
 *
 */
object Util {
  /**
   * Prop 及相关方法待改造，不捕获外部变量，改造函数式
   */
  private val _prop_ = new java.util.Properties()
  try {
	  _prop_.load(this.getClass.getClassLoader.getResourceAsStream("runtime.properties"))
  } catch { case t: Throwable => t.printStackTrace() }
    
  def prop(key: String) = Option(_prop_.getProperty(key)).getOrElse("Missing key --> " + key)
  
  val propInt = (key: String) => Integer.valueOf(prop(key))

  def hasConstants(keys: String*): Boolean = {
    var f = true
    keys.foreach(__ => if(!_prop_.containsKey(__)) f = false)
    f
  }
 
  import com.sasaki.lp.enums.E._
  def keyFrom(k: String/*<-- key pattern*/, s: String/*<-- source string*/): String = {
    val pairs: Array[(String, String)] = s.split($).map(__ => (__.split(->)(0), __.split(->)(1)))
    
    @annotation.tailrec
    def loop(n: Int, k: String): String = 
      if(n >= pairs.length) null
      else if(pairs(n)._1 == k) pairs(n)._2
      else loop(n + 1, k)
    
    loop(0, k)
  }
  
  import org.json4s._
  import org.json4s.JsonAST.JValue
  import org.json4s.JsonAST.JString
  import org.json4s.jackson.JsonMethods._
  implicit val formats = org.json4s.DefaultFormats
  /**
   * @param k 		key Pattern
   * @param json	json String
   */
  def extractFrom(k: String/*<-- key pattern*/, json: String/*<-- json String*/): Any = {
    val o = parse(json, true) \ k
    o match {
      case JString(_)   => o.extract[String]
      case JBool(_)     => o.extract[Boolean]
      case JDecimal(_) | JInt(_)  => o.extract[Integer]
      case JDouble(_)   => o.extract[Double]
      case JArray(_)    => o.extract[Array[Any]]
      case JObject(_)   => o.extract[Object]
      case JNothing | JNull => ""
    }
  }
  
  def between() = {
    
  }
  
  def has() = {
    
  }

  
  def main(args: Array[String]): Unit = {
//    println(Util.prop("kafka.metadata.broker.list"))
    println(hasConstants("jdbc.url", "" ))
//    println(_prop_.containsKey("jdbc.url"))
  }
  
  
}