import scala.collection.mutable.ListBuffer
import java.util.regex.Matcher

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-08 上午11:31:46
 * @Description 工具类
 */
package object independent {

  val $e = ""        // empty
  val $s = " "       // space
  val $p = "."       // point
  val $n = null      // null
  val $u = "_"       // underline
  
  def MUST_NOT_BE_NULL(s: AnyRef = "Argument") = s"$s must not be null!"
  def MUST_NOT_BE_EMPTY(s: AnyRef = "Argument") = s"$s must not be empty!"
    
  def isNull(o: Any) = null == o
  
  def nonNull(o: Any) = !isNull(o)
  
  @deprecated
  def nonEmpty(o: Any) =
    nonNull(o) && (o.getClass().getSimpleName match {
      case "String" => o != $e
      //      case "???" =>
      case _        => false
    })
    
  // ------------------------------------ Invoke Template --------------------------------------------
  
  def invokeWithRequire[T](f_x: () => Boolean, slogan: String)(g_x: () => T) = {
    require(f_x(), slogan)
    g_x()
  }

  def invokeNonNull[T](args: Any*)(g_x: () => T) =
    invokeWithRequire(() => args.forall(nonNull _), MUST_NOT_BE_NULL(args))(g_x)
    
  def invokeNonEmpty[T](args: Any*)(g_x: () => T) =
    invokeWithRequire(() => args.forall(nonEmpty _), MUST_NOT_BE_NULL(args))(g_x)
    
  // -------------------------------------------------------------------------------------------------

  /**
   * 去除指定字符
   */
  def erase(o: String, s: String): String = o.replace(s, $e)

  /**
   * 去除序列最后一个元素
   */
  def body[T](l: Seq[T]) = l.slice(0, l.size - 1)
  
  def eraseMultiple(o: String, ss: String*): String =
    invokeWithRequire(() => nonNull(ss), MUST_NOT_BE_NULL(s"Target character: $ss")) { () =>
      def loop(o: String, i: Int): String =
        if (i != ss.length - 1)
          loop(erase(o, ss(i)), i + 1)
        else
          erase(o, ss(i))
          
      loop(o, 0)
    }
  
  def peek[T](o: Any): T = { println(o); o.asInstanceOf[T] }

  /**
   * 判断两个字符串等价，包含的每个字符数相等
   *
   * ab && ab && ba 		-> true
   * ab && b						-> false
   *
   */
  def equalString(_s: String, s: String) = {
    lazy val _ss = _s.split($e).distinct
    lazy val ss = s.split($e).distinct
    if ({
      // 字符串长度
      _s.length() != s.length() ||
      // 去重后字符数组长度
      _ss.size != ss.size
    })
      false
    else
      // _ss 中每个字符在 ss 中皆存在
      _ss.forall(ss contains _)
  }

  def trimBothSide(s: String) =
    invokeNonEmpty(s) { () =>
      if (s.length() == 1) $e else s.substring(1, s.length() - 1)
    }

  def md5(s: String) =
    if (nonNull(s)) {
      val digest = java.security.MessageDigest.getInstance("MD5")
      digest.digest(s.getBytes).map("%02x" format _).mkString
    } else null

  /**
   * JSON检验器
   * 仅检验字符串是否满足JSON标准
   */
//  def isJson(json: String) = scala.util.parsing.json.JSON.parseFull(json) match {
//    case Some(map: Map[_, Any]) => true
//    case _                      => println(s"Invalid json --> $json"); false
//  }

  private type JTimestamp = java.sql.Timestamp
  private type JSimpleDateFormat = java.text.SimpleDateFormat
  private type JDate = java.util.Date
  
  def timestamp(s: String): Option[java.sql.Timestamp] =
    if (nonEmpty(s))
      if (s.contains($s))
        Option(new JTimestamp(new JSimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(s).getTime))
      else
        Option(new JTimestamp(new JSimpleDateFormat("yyyy-MM-dd").parse(s).getTime))
    else None

  def currentTimeMillis = /*java.time.Instant.now().toEpochMilli()*/ 
     java.time.Clock.systemUTC().millis()

  def currentFormatTime = 
    new JSimpleDateFormat("yyyy-MM-dd hh:mm:ss")
      .format(new JDate(currentTimeMillis))

  def currentFormatDate = 
    new java.text.SimpleDateFormat("yyyy-MM-dd")
      .format(new JDate(currentTimeMillis))

//  def formatDuration(durationTimeMillis: Long) = 
//    org.apache.commons.lang3.time.DurationFormatUtils.formatDuration(durationTimeMillis, "HH:mm:ss", true)
    
//  def formatUntilDuration(lastTimeMillis: Long) = formatDuration(currentTimeMillis - lastTimeMillis)

  /**
   * 平行映射
   * 对两组序列1->1 映射产出一组序列值
   */
  @deprecated
  def paraSeq[R, S, T](r: Seq[R], s: Seq[S])(f_x: (R, S) => T): Seq[T] = {
    require(r.size == s.size, "Seq[R] and Seq[S] must have equal size!")
    for (i <- 0 until r.size) yield f_x(r(i), s(i))
  }
}

/**
 * 反射
 */
package object reflect {

  import scala.reflect.runtime.universe._

  type SA = scala.annotation.StaticAnnotation

  def clazz[T: Manifest] = symbolOf[T].asClass
  //  def mirror[T: Manifest] = runtimeMirror(getClass.getClassLoader)

  /**
   * 返回不带$类简称
   */
  def getSimpleName[T](t: T): String = {
    val o = t.getClass().getSimpleName
    if (o.contains("$")) o.replaceAll("\\$", independent.$e) else o
  }
  
  def buildInstance[T: Manifest](args: Any*) =
    runtimeMirror(getClass.getClassLoader)
      .reflectClass(clazz[T])
      .reflectConstructor(extractConstructor[T]).apply(args: _*)
      .asInstanceOf[T]

  def extractConstructor[T: Manifest] = typeOf[T].decl(TermName("<init>" /*Name of constructor.*/ )).asMethod

  @deprecated
  def extractClass[T: Manifest] = implicitly[Manifest[T]].erasure

  @deprecated
  def extractFieldNames[T: Manifest]: Seq[String] = extractClass[T].getDeclaredFields.map(_.getName)

  @deprecated
  def extractFieldNames___Types[T: Manifest] = 
    extractClass[T]
      .getDeclaredFields
      .map(o => (o.getName, o.getType.toString()
      .replace("class ", independent.$e)))

  @deprecated
  def extractFieldName___Type[T: Manifest] = 
    extractClass[T]
      .getDeclaredFields
      .map(o => (o.getName, o.getType))

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

  def extractSingleFieldWhileAnnotation[T: Manifest, A <: SA: TypeTag] = 
    extractField2Annotations[T]
      .find(_._2.exists(o => fxTypeIs[A](o.tree.tpe)))
      .map(_._1)

  def extractListFieldWhileAnnotation[T: Manifest, A <: SA: TypeTag] = ???

  def existsAnnotationFromType[T: Manifest, A <: SA: TypeTag] = 
    extractClassAnnotations[T]
      .exists(o => fxTypeIs[A](o.tree.tpe))

  def existsAnnotationFromField[T: Manifest, A <: SA: TypeTag](f: String) = {
    val opField___Annotations = extractField2Annotations[T].find(_._1 == f)
    opField___Annotations match {
      case Some(_) => opField___Annotations.get._2.exists(o => fxTypeIs[A](o.tree.tpe))
      case None    => false
    }
  }

  def fxTypeIs[T: TypeTag](t: Type) = t =:= typeOf[T]
}

/**
 * 正则
 */
package object regex {
  import independent._

  protected val buildMatcher = (s: String, regex: String) =>
    java.util.regex.Pattern.compile(regex).matcher(s)

  def extractMatched(s: String, regex: String): String = {
    val matcher = buildMatcher(s, regex)
    if (matcher.find()) matcher.group(1) else independent.$e
  }

  def extractMatchedMultiple(s: String, regex: String): Seq[String] = {
    val matcher = buildMatcher(s, regex)
    val list = new ListBuffer[String]

    //    				def loop(i: Int): ListBuffer[String] =
    //    				if (matcher.find()) {
    //    					list.append(matcher.group(i))
    //    					loop(i + 1)
    //    				} else
    //    					list
    //    					
    //    		loop(0)

    var i: java.lang.Integer = 0
    i.synchronized {
      while (matcher.find()) {
        list.append(matcher.group(i))
        i + 1
      }
    }
    list.toList
  }

  /**
   * 判断一个字符串是否全为数字  
   */
  def isDigit(s: String) = invokeNonNull(s)(() => s.matches("[0-9]{1,}"))

  /**
   * 提取数字  
   */
  def extractNumbers(s: String): Seq[Int] = extractMatchedMultiple(s, "\\d+").map(_ toInt)

  /**
   * 提取非数字  
   */
  def extractNonNumbers(s: String) = extractMatchedMultiple(s, "\\D+")
}

