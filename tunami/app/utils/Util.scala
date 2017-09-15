package utils

/**
 *
 */

class UtilProperties(val pFile: String = null) extends java.util.Properties {
  import independent._
  
  val p = new java.util.Properties
  try {
    if(nonNull(pFile))
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