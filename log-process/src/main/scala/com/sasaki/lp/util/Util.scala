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
  import scala.reflect.runtime.universe._
  def extractFrom[T <: Any](k: String/*<-- key pattern*/, json: String/*<-- json String*/) = {
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
  
  def has(data: String, dKey: String, param: String, pKey: String): Boolean = {
    val pValue = keyFrom(pKey, param)
    if(pValue == null) 
      return true // <-- ???
    
    val pValues = pValue.split(->)
    
    val dValue = keyFrom(dKey, data)
    if(dValue.nonEmpty) {
      val dValues = dValue.split(->)
      pValues.foreach(__ => /*dValues.foreach(___ => if(___.equals(__)) return true)*/ println(__))
    }
    
    false
  }

  class GenericClass[T](value:T)
  
  import scala.reflect.runtime.universe._
  def checkType[T](generic: GenericClass[T])(implicit t: TypeTag[T]):Unit = t.tpe match {
    case tpe if tpe =:= typeOf[Int] =>
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
    	val data = "name->sasaki$age->20";
		  val parameter = "name->sasaki$age->20$a->1,2,3,4$b->43,55,32,20";

//		  println(keyFrom("b", parameter))
//		  println(has(data, "age", parameter, "b"))
//		  println("1$23".split('$')(1))
		  println(checkType(new GenericClass(1))) 
		  
  }
  
  
}