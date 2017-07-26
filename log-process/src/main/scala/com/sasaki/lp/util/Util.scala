package com.sasaki.lp.util

import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonAST.JString
import org.json4s.jackson.JsonMethods._

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
  /**
   * Fetch value from String by key
   */
  def vFrom(key: String/*<-- key pattern*/, str: String/*<-- source string*/): String = {
    val pairs: Array[(String, String)] = str.split($).map(__ => (__.split(->)(0), __.split(->)(1)))
    
    @annotation.tailrec
    def loop(n: Int, k: String): String = 
      if(n >= pairs.length) ""
      else if(pairs(n)._1 == k) pairs(n)._2
      else loop(n + 1, k)
    
    loop(0, key)
  }
  

  implicit val formats = org.json4s.DefaultFormats
  
  /**
   * @param k 		key Pattern
   * @param json	json String
   * 该方法暂未解决返回值的泛型自动转换问题，不适用
   */
  import scala.reflect.runtime.universe._
  @deprecated
  def extractFrom(k: String/*<-- key pattern*/, json: String/*<-- json String*/) = {
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

  /**
   * dKeys 与 pKeys 两组值中有任意一个值相同即为true
   */
  def include(data: String, dKey: String, param: String, pKey: String): Boolean = {
    val pValue = vFrom(pKey, param)
    if(pValue == null) 
      return true // <-- ???
    
    val pValues = pValue.split(/)
    val dValue = vFrom(dKey, data)
    
    if(dValue.nonEmpty) 
      pValues.foreach(__ => dValue.split(/).foreach(___ => if(___.equals(__)) return true))
    
    false
  }
  
  /**
   * 
   */
  def equal(data: String, dKey: String, param: String, pKey: String): Boolean = {
    val pValue = vFrom(pKey, param)
    if(pValue == null) 
      return true // <-- ???    
      
    val dValue = vFrom(dKey, data)
    if(dValue.nonEmpty)
      if(dValue == pValue)
        return true
    
    false
  }
    
  /**
   * 
   */
  def between(data: String, dKey: String, param: String, pKey: String, pKey_ : String): Boolean = {
    val pValue  = vFrom(pKey, param)
    val pValue_ = vFrom(pKey_, param)
    if(pValue == null || pValue_ == null) 
      return true // <-- ???
    
    val i_pValue  = pValue.toInt
    val i_pValue_ = pValue_.toInt
    
    val dValue = vFrom(dKey, data)
    if(dValue.nonEmpty) {
      val i_dValue = dValue.toInt
      if(i_dValue >= i_pValue && i_dValue <= i_pValue_)
        return true
    }
      
    false
  }
  
  class GenericClass[T](value:T)
  
  def checkType[T](generic: GenericClass[T])
    (implicit t: scala.reflect.runtime.universe.TypeTag[T]/*该类型参数在编译期间会将泛型的信息加入到字节码*/):Unit = t.tpe match {
    case tpe if tpe =:=/* =:=对Type类型比较，判断是否是同一类型，<:<判断前者是不是继承后者*/ typeOf[Int] =>
      println("a int generic class")
    case tpe if tpe =:= typeOf[String] =>
      println("a string generic class")
    case _ =>
      println("a unknown generic class")
  }
  
  def main(args: Array[String]): Unit = {
//    println(Util.prop("kafka.metadata.broker.list"))
//    println(hasConstants("jdbc.url", "" ))
//    println(_prop_.containsKey("jdbc.url"))
    	val data = "name->sasaki$age->59"
		  val parameter = "name->sasaki$age->20$a->1,2,3,4$b->43,55,32,20$fromAge->20$toAge->59"
      val json = """
      {"id": 1, "salary": 234.22, "flag": true, "name": "sasaki", "seq": [1, 2, 3, 4]}
      """
    
//		  println(vFrom("b", parameter))
//		  println(include(data, "age", parameter, "b"))
//		  println(equal(data, "name", parameter, "name"))
      
//		  println("1$23".split('$')(1))
//		  println(checkType(new GenericClass("1"))) 
//      val id = extractFrom("id", json)
//      println(between(data, "age", parameter, "fromAge", "toAge"))
      
      def compare(x: Int, y: Int): Boolean = x > y 
      println(List(1, 2, 3, 4, 5).filter { x =>  
//          compare(x, 2) && compare(x, 3)
            x>2 && x>3 && false && false     
    	}
      )
      
    println("yyyy-MM-dd hh:mm:ss".split(' ')(0) + $__ + "yyyy-MM-dd hh:mm:ss".split(' ')(1).split(':')(0))  
//      println("aa__bb".split("__")(1))
  }
  
  
}