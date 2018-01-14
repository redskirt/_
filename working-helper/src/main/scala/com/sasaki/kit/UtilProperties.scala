package com.sasaki.kit

import java.util.Properties
/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2018-01-14 17:28:47
 * @Description 
 */
class UtilProperties(val pFile: String = null) extends Properties {
  import com.sasaki.packages.independent._

  private val self = this
  private val p = new Properties

  try {
    if (nonNull(pFile))
      p.load(getClass.getClassLoader.getResourceAsStream(pFile))
  } catch { 
    case t: Throwable => t.printStackTrace() 
  }

  def _put(k: String, v: Object) = { self.put(k, v); self }
 
  // TODO ??? Option用法不合适
  def _prop(key: String) = Option(p.getProperty(key))

  // 同上，尝试lift
  def _propInt(key: String) = Integer.valueOf(_prop(key).getOrElse("0"))

  def _containsThese(keys: String*): Boolean = 
    keys.map(p containsKey _).reduce(_ && _)

}

object Test {
  def main(args: Array[String]): Unit = {

  }
}