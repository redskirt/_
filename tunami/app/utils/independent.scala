import scala.reflect.ClassTag
import java.util.regex.Pattern

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-08 上午11:31:46
 * @Description 
 */
package object independent {
  
  def isNull(o: Any) = null == o
  def nonNull(o: Any) = !isNull(o)

  def isEmpty(o: Any) = isNull(o) && (getSimpleName(o) match {
    case "String" => o == ""    
    case "???" => ??? // TODO: and more... 
    case _ => false
  })
  
  def nonEmpty(o: Any) = !isEmpty(o)
 
  def getMatched(str: String, regex: String): String = {
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(str)
    
    if (matcher.find()) matcher.group(1) else ""
  }  
  
  /**
   * 返回不带$类简称
   */
  def getSimpleName[T](t: T): String = { 
    val o = t.getClass().getSimpleName
    if(o.contains("$")) o.replaceAll("\\$", "") else o 
  }

  def md5($: String) = {
    val digest = java.security.MessageDigest.getInstance("MD5")
    digest.digest($.getBytes).map("%02x".format(_)).mkString
  }

  def main(args: Array[String]): Unit = {
    println(getSimpleName(this))
  }
  
}