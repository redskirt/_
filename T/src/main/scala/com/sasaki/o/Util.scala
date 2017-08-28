package com.sasaki.o
import java.util.regex.Pattern

/**
 *
 */
object Util {
  
  def nonNull(o: Any) = null != o

  def getMatched(str: String, regex: String): String = {
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(str)
    
    if (matcher.find())
      return matcher.group(1)
    ""
  }

  def nonEmpty(o: Any) = 
    nonNull(o) && (o.getClass().getSimpleName match {
      case "String" => o != ""
//      case "???" => 
      case _ => false
    })
  
  /**
   * 返回不带$类简称
   */
  def getSimpleName[T](t: T): String = { 
    val o = t.getClass().getSimpleName
    if(o.contains("$")) o.replaceAll("\\$", "") else o 
  } 
  
  def main(args: Array[String]): Unit = {
//    val p = new org.sh.sbdp.slap.streaming.util.UtilProperties()
    println(getSimpleName(this))
//    println("swer$".replaceAll("\\$", ""))
  }
  
}

class UtilProperties(val pFile: String = null) extends java.util.Properties {
  val p = new java.util.Properties
  try {
    if(Util.nonNull(pFile))
	    p.load(this.getClass.getClassLoader.getResourceAsStream(pFile))
  } catch { case t: Throwable => t.printStackTrace() }
    
  def _put(k: String, v: Object): UtilProperties = { this.put(k, v); this }
  
  def _prop(key: String) = Option(p.getProperty(key)).getOrElse("Missing key --> " + key)
  
  val _propInt = (key: String) => Integer.valueOf(_prop(key))

  def _hasConstants(keys: String*): Boolean = {
    var _f = true
    keys.foreach(__ => if(!p.containsKey(__)) _f = false)
    _f
  }
}