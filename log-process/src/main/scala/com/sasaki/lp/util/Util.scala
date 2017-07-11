package com.sasaki.lp.util

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
  
  def main(args: Array[String]): Unit = {
//    println(Util.prop("kafka.metadata.broker.list"))
    println(hasConstants("jdbc.url", "" ))
//    println(_prop_.containsKey("jdbc.url"))
  }
  
  
}