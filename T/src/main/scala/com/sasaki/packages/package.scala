import scala.reflect.ClassTag
import java.util.regex.Pattern

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-08 上午11:31:46
 * @Description 
 */
package object independent {
  
  private[this] val empty = ""
  private[this] val space = " "
  
  def isNull(o: Any) = null == o
  def nonNull(o: Any) = !isNull(o)

  def nonEmpty(o: Any) = 
    nonNull(o) && (o.getClass().getSimpleName match {
      case "String" => o != empty
//      case "???" => 
      case _ => false
    })

  def wipe(o:String, s: String) = o.replace(s, empty)
    
  def peek[T](o: Any) = { println(o); o.asInstanceOf[T] }

  def getMatched(str: String, regex: String): String = {
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(str)
    
    if (matcher.find()) matcher.group(1) else empty
  }
  
  /**
   * 返回不带$类简称
   */
  def getSimpleName[T](t: T): String = { 
    val o = t.getClass().getSimpleName
    if(o.contains("$")) o.replaceAll("\\$", empty) else o 
  }

  def trimBothSide(s: String) = {
    require(nonEmpty(s), "String is Empty!")
    if(s.length() == 1) empty else s.substring(1, s.length() - 1)
  }

  def md5(s: String) = {
    if(nonNull(s)) {
    	val digest = java.security.MessageDigest.getInstance("MD5")
    	digest.digest(s.getBytes).map("%02x".format(_)).mkString
    } else null
  } 
  
  /**
   * JSON检验器
   * 仅检验字符串是否满足JSON标准
   */
  import scala.util.parsing.json.JSON
  def invalidJson(json: String) = JSON.parseFull(json) match {
    case Some(map: Map[_, Any]) => true
    case _ => println(s"Invalid json --> $json"); false
  }
  
  /**
   * 不通过实例直接获得类属性
   */
  def extractFieldNames[T<:Product:Manifest] = implicitly[Manifest[T]].erasure.getDeclaredFields.map(_.getName)
  
  /**
   * 不通过实例直接获得类 属性___类型 （默认分隔符）
   * 该方法场景特殊，不建议直接应用
   */
  @deprecated
  def extractFieldNamesSeparatorTypes[T<:Product:Manifest](separator: String = "___") = implicitly[Manifest[T]].erasure.getDeclaredFields.map(o => o.getName + separator + o.getType.toString().replace("class ", empty))
  
  /**
   * 不通过实例直接获得类名
   */
  def extractSimpleName[T<:Product:Manifest] = implicitly[Manifest[T]].erasure.getSimpleName
  
  def extractFullName[T<:Product:Manifest] = implicitly[Manifest[T]].erasure.getName

  def timestamp(s: String): Option[java.sql.Timestamp] =
    if (nonEmpty(s))
      if (s.contains(space)) 
        Option(new java.sql.Timestamp(new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(s).getTime))
      else 
        Option(new java.sql.Timestamp(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(s).getTime))
    else None
 
  def formatDuration(durationTimeMillis: Long) = org.apache.commons.lang3.time.DurationFormatUtils.formatDuration(durationTimeMillis, "HH:mm:ss", true)  
  def formatUntilDuration(lastTimeMillis: Long) = formatDuration(/*java.time.Instant.now().toEpochMilli()*/java.time.Clock.systemUTC().millis() - lastTimeMillis)
  
}