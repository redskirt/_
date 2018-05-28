package com.sasaki.wp.util

import java.util.regex.Pattern

/**
 *
 */
object Util {
  
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
  
  def nonNull(o: Any) = null != o

  def getMatched(str: String, regex: String): String = {
    val pattern = Pattern.compile(regex, Pattern.DOTALL)
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
  
  
  import java.io.File
  def listFiles(url: String) = {
    val t = new File(url)
    if(t.isDirectory()) 
      t.listFiles().filterNot(_.isDirectory())
    else
      Array(t)
  }
    
  def main(args: Array[String]): Unit = {
//    println(Util.prop("kafka.metadata.broker.list"))
//    println(hasConstants("jdbc.url", "" ))
//    println(_prop_.containsKey("jdbc.url"))
    // 北京缺 dbImage_ID-19023_No-1.jpeg
    listFiles("/Users/sasaki/vsh/bj")
    .filter(_.getName.contains("dbImage"))
//    .foreach { o => 
//      val name = o.getName
//      println(name)
//      println(name.substring(11, name.lastIndexOf("_")))
//    }
    .foreach { o =>
      if(200 > o.length()) {
        println(o.getName)
        o.delete()
      }
    }
    
  }
  
  
}