import scala.reflect.ClassTag
import java.util.regex.Pattern

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-08 上午11:31:46
 * @Description 
 */
package object independent {
  
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

  def getSimpleName2[T: ClassTag](t: T): String = {
    val o = t.getClass().getSimpleName
    if (o.contains("$")) o.replaceAll("\\$", "") else o
  } 
  
  def main(args: Array[String]): Unit = {
//    val p = new org.sh.sbdp.slap.streaming.util.UtilProperties()
    println(getSimpleName(this))
//    println("swer$".replaceAll("\\$", ""))
  }
  
}