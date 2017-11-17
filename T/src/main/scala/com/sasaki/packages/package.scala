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

  @deprecated
  def nonEmpty(o: Any) =
    nonNull(o) && (o.getClass().getSimpleName match {
      case "String" => o != empty
      //      case "???" =>
      case _        => false
    })

  /**
   * 去除指定字符  
   */
  def wipe(o:String, s: String) = o.replace(s, empty)
    
  def peek[T](o: Any) = { println(o); o.asInstanceOf[T] }

  import java.util.regex.Pattern
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

  def md5(s: String) = 
    if(nonNull(s)) {
    	val digest = java.security.MessageDigest.getInstance("MD5")
    	digest.digest(s.getBytes).map("%02x" format _).mkString
    } else null
  
  /**
   * JSON检验器
   * 仅检验字符串是否满足JSON标准
   */
  def invalidJson(json: String) = scala.util.parsing.json.JSON.parseFull(json) match {
    case Some(map: Map[_, Any]) => true
    case _ => println(s"Invalid json --> $json"); false
  }
  
  def timestamp(s: String): Option[java.sql.Timestamp] =
    if (nonEmpty(s))
      if (s.contains(space)) 
        Option(new java.sql.Timestamp(new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(s).getTime))
      else 
        Option(new java.sql.Timestamp(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(s).getTime))
    else None
 
  def formatDuration(durationTimeMillis: Long) = org.apache.commons.lang3.time.DurationFormatUtils.formatDuration(durationTimeMillis, "HH:mm:ss", true)  
  def formatUntilDuration(lastTimeMillis: Long) = formatDuration(/*java.time.Instant.now().toEpochMilli()*/java.time.Clock.systemUTC().millis() - lastTimeMillis)

  /**
   * 平行映射
   * 对两组序列1->1 映射产出一组序列值
   */
  @deprecated
  def paraSeq[R, S, T](r: Seq[R], s: Seq[S])(f_x: (R, S) => T): Seq[T] = {
    require(r.size == s.size, "Seq[R] and Seq[S] must have equal size!")
    for (i <- 0 until r.size) yield f_x(r(i), s(i))
  }
    	
  // ----------------------------   反射     -------------------------------
  import _root_.scala.reflect.runtime.universe._
  
  type SA = scala.annotation.StaticAnnotation

  def clazz[T: Manifest] = symbolOf[T].asClass
//  def mirror[T: Manifest] = runtimeMirror(getClass.getClassLoader)
  
  def buildInstance[T: Manifest](args: Any*) = 
    runtimeMirror(getClass.getClassLoader).reflectClass(clazz[T]).reflectConstructor(extractConstructor[T]).apply(args: _*).asInstanceOf[T]
		  
  def extractConstructor[T: Manifest] = typeOf[T].decl(TermName("<init>"/*Name of constructor.*/)).asMethod
  
  @deprecated
  def extractClass[T: Manifest] = implicitly[Manifest[T]].erasure
  
  @deprecated
  def extractFieldNames[T: Manifest]: Seq[String] = extractClass[T].getDeclaredFields.map(_.getName)
  
  @deprecated
  def extractFieldNames___Types[T: Manifest] = extractClass[T].getDeclaredFields.map(o => (o.getName, o.getType.toString().replace("class ", empty)))
  
  @deprecated
  def extractFieldName___Type[T: Manifest] = extractClass[T].getDeclaredFields.map(o => (o.getName, o.getType))
  
  @deprecated
  def extractSimpleName[T: Manifest] = extractClass[T].getSimpleName
  
  @deprecated
  def extractFullName[T: Manifest] = extractClass[T].getName
  
  def extractType[T: Manifest] = symbolOf[T].asClass.primaryConstructor.typeSignature
    
  def extractClassAnnotations[T: Manifest] = symbolOf[T].asClass.annotations
	
  def extractFieldAnnotations[T: Manifest] = {
    val paramLists: List[List[Symbol]] = extractType[T].paramLists
    paramLists.head.map(_ annotations)
  }
  
  def extractTypes[T: Manifest] = {
    val paramLists: List[List[Symbol]] = extractType[T].paramLists
    paramLists.head.map(_ typeSignature)
  }

  def extractField2Annotations[T: Manifest] = extractFieldNames[T] zip extractFieldAnnotations[T]
		  
	def extractField2Type[T: Manifest] = extractFieldNames[T] zip extractTypes[T]
  
  def extractSingleFieldWhileAnnotation[T: Manifest, A <: SA: TypeTag] = extractField2Annotations[T].find(_._2.exists(o => fxTypeIs[A](o.tree.tpe))).map(_._1)
  
  def extractListFieldWhileAnnotation[T: Manifest, A <: SA: TypeTag] = ???

  def existsAnnotationFromField[T: Manifest, A <: SA: TypeTag](f: String) = {
    val opField___Annotations = extractField2Annotations[T].find(_._1 == f)
    opField___Annotations match {
      case Some(_) => opField___Annotations.get._2.exists(o => fxTypeIs[A](o.tree.tpe))
      case None    => false
    }
  }
  
  def fxTypeIs[T : TypeTag](t: Type) = t =:= typeOf[T]
  
  // -------------------------------------------------------------------
}