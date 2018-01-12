package com.sasaki.packages

import scala.reflect.runtime.universe._

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-08 上午11:31:46
 * @Description 二方引入库
 */
/**
 * 工具
 */
package object independent {
  
  import reflect._
  import constant._
  import constant.original._
  
  def isNull(o: A) = null == o
  
  def nonNull(o: A) = !isNull(o)

  def nonEmpty[T: TT](o: T) =
    nonNull(o) && (typeOf[T] match {
      case t if t =:= typeOf[String]    => o.asInstanceOf[String] nonEmpty
      case t if t <:< typeOf[Seq[_]]    => o.asInstanceOf[Seq[_]] nonEmpty
      case t if t <:< typeOf[Map[_, _]] => o.asInstanceOf[Map[_, _]] nonEmpty
      case _                            => throw new IllegalArgumentException(s"$$independent$$nonEmpty$$ Unknown type ${typeOf[T]}.")
    })
    
  // ------------------------------------ Invoke Template --------------------------------------------
    
  def MUST_NOT_BE_NOTHING = "Type parameters must not be Nothing!"
  def MUST_NOT_BE_NULL(s: R = "Argument") = s"$s must not be null!"
  def MUST_NOT_BE_EMPTY(s: R = "Argument") = s"$s must not be empty!"
  
  def invokeVerify[T](f_x: () => Boolean, slogan: String)(g_x: () => T) = {
    require(f_x(), slogan)
    g_x()
  }

  @deprecated("该函数编译时非类型安全，慎用。")
  def invokeNonNothing[E: TT, T/*Which return type of function*/](g_x: () => T) =
    invokeVerify(() => !typeNothing[E], MUST_NOT_BE_NOTHING)(g_x)
 
  /**
   * 该函数对目标检验值仅提供一个泛型约束，所以多参数检验时，仅适用可变参数列表类型一致的情况。  
   */
  def invokeNonNull[E <: R: TT, T](args: E*)(g_x: () => T) =
    invokeVerify(() => args.forall(nonNull _), MUST_NOT_BE_NULL(args))(g_x)
   
  /**
   * @see Above.
   */
  def invokeNonEmpty[E <: R: TT, T](args: E*)(g_x: () => T) =
    invokeVerify(() => args.forall(nonEmpty _), MUST_NOT_BE_EMPTY(args))(g_x)
    
  // -------------------------------------------------------------------------------------------------

  /**
   * 去除序列最后一个元素
   */
  def body[T](l: Seq[T]) = l.slice(0, l.size - 1)
  
  /**
   * 去除指定字符，对""字符无效
   */
  def erase(that: String, specify: String): String = 
    invokeNonNull(that, specify)(() => that.replace(specify, $e))
  
  /**
   * @see def erase(o: String, s: String): String
   */
  def eraseMultiple(o: String, ss: String*): String =
    invokeVerify(() => nonNull(ss), MUST_NOT_BE_NULL(s"Target character: $ss")) { () =>
      def loop(o: String, i: Int): String =
        if (i != ss.length - 1)
          loop(erase(o, ss(i)), i + 1)
        else
          erase(o, ss(i))
          
      loop(o, 0)
    }
  
  def peek[T: TT](o: T): T = { println(o); o }
  
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
  
  /**
   * 返回不带$类简称
   */
  def getSimpleName[T](t: T): String = {
    val o = t.getClass().getSimpleName
    if (o.contains("$")) o.replaceAll("\\$", $e) else o
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
  def isJson(json: String) = scala.util.parsing.json.JSON.parseFull(json) match {
    case Some(map: Map[_, A]) => true
    case _                      => println(s"Invalid json --> $json"); false
  }

  protected val PATTERN_DATE      = "yyyy-MM-dd"
  protected val PATTERN_TIMESTAMP = "yyyy-MM-dd hh:mm:ss"
  
  def timestamp(s: String): Option[JTimestamp] =
    if (nonEmpty(s))
      if (s.contains($s))
        Some(new JTimestamp(new JSimpleDateFormat(PATTERN_TIMESTAMP).parse(s).getTime))
      else
        Some(new JTimestamp(new JSimpleDateFormat(PATTERN_DATE).parse(s).getTime))
    else 
      None

  def currentTimeMillis = /*java.time.Instant.now().toEpochMilli()*/ 
     java.time.Clock.systemUTC().millis()

  def currentFormatTime = 
    new JSimpleDateFormat(PATTERN_TIMESTAMP).format(new JDate(currentTimeMillis))

  def currentFormatDate = 
    new java.text.SimpleDateFormat(PATTERN_DATE).format(new JDate(currentTimeMillis))

  def formatDuration(durationTimeMillis: Long) = 
    org.apache.commons.lang3.time.DurationFormatUtils.formatDuration(durationTimeMillis, "HH:mm:ss", true)
    
  def formatUntilDuration(lastTimeMillis: Long) = formatDuration(currentTimeMillis - lastTimeMillis)

  /**
   * 平行映射
   * 对两组序列1->1 映射产出一组序列值
   */
  @deprecated
  def paraSeq[R, S, T](r: Seq[R], s: Seq[S])(f_x: (R, S) => T): Seq[T] =
    invokeVerify(() => r.size == s.size, "Seq[R] and Seq[S] must have equal size!") { () =>
      for (i <- 0 until r.size) yield f_x(r(i), s(i))
    }
}

/**
 * 反射
 */
package object reflect {
  
  import constant._
  import constant.original._
  
  /**
   * 示例：file:/H:/git-repo/_/working-helper/target/classes/
   */
  def classpath = 
    independent.erase(getClass.getClassLoader.getResource($e).toString, "file:/")
  
  def clazz[T: TT]: ClassSymbol = symbolOf[T].asClass

  def buildInstance[T: TT](args: A*): T =
    runtimeMirror(getClass.getClassLoader)
      .reflectClass(clazz[T])
      .reflectConstructor(extractConstructor[T])
      .apply(args: _*)
      .asInstanceOf[T]

  def extractConstructor[T: TT]: MethodSymbol = 
    typeOf[T].decl(termNames.CONSTRUCTOR).asMethod

  def typeNothing[T: TT]: Boolean = typeEqual[T, Nothing]
  
  def typeIs[T: TT](t: Type): Boolean = t =:= typeOf[T]
  
  def typeEqual[E: TT, T: TT]: Boolean = typeOf[E] =:= typeOf[T]
  
  def typeFrom[E: TT, T: TT]: Boolean = typeOf[E] <:< typeOf[T]

  /**
   * 仅适用case class
   */
  def extractFieldNames[T: TT]: Seq[String] =
    typeOf[T].members.collect {
      case m: MethodSymbol if m.isCaseAccessor => m.name.toString()
    }.toList.reverse

  def extractSimpleName[T: TT](t: Class[T]): String = t.getSimpleName
  
  def extractSimpleName[T: TT]: String = extractFullName[T].split($p).last

  def extractFullName[T: TT](t: Class[T]): String = t.getName
  
  def extractFullName[T: TT]: String = typeOf[T].toString()

  def extractTypes[T: TT]: List[Type] =
    extractSymbolList[T].head.map(_ typeSignature)

  private def extractSymbolList[T: TT]: List[List[Symbol]] =
    symbolOf[T].asClass.primaryConstructor.typeSignature.paramLists

  def extractClassAnnotations[T: TT]: List[Annotation] = 
    symbolOf[T].asClass.annotations

  def extractFieldAnnotations[T: TT]: List[List[Annotation]] = 
    extractSymbolList[T].head.map(_ annotations)

  def extractField2Annotations[T: TT]: Seq[(String, List[Annotation])] = 
    extractFieldNames[T] zip extractFieldAnnotations[T]

  def extractField2Type[T: TT]: Seq[(String, Type)] =
    extractFieldNames[T] zip extractTypes[T]

  def extractSingleFieldWhileAnnotation[T: TT, E <: SA: TT]: Option[String] =
    extractField2Annotations[T]
      .find(_._2.exists(o => typeIs[E](o.tree.tpe)))
      .map(_._1)

  def extractListFieldWhileAnnotation[T: TT, E <: SA: TT] = ???

  def existsAnnotationFromType[T: TT, E <: SA: TT]: Boolean =
    extractClassAnnotations[T]
      .exists(o => typeIs[E](o.tree.tpe))

  def existsAnnotationFromField[T: TT, E <: SA: TT](f: String): Boolean = {
    val opField___Annotations = extractField2Annotations[T].find(_._1 == f)
    opField___Annotations match {
      case Some(_) => opField___Annotations.get._2.exists(o => typeIs[E](o.tree.tpe))
      case None    => false
    }
  }
}

/**
 * 正则
 */
package object regex {

  protected lazy val buildMatcher = (s: String, regex: String) =>
    java.util.regex.Pattern.compile(regex).matcher(s)

  def extractMatched(s: String, regex: String): String = {
    val matcher = buildMatcher(s, regex)
    if (matcher.find()) matcher.group(1) else constant.$e
  }

  def extractMatchedMultiple(s: String, regex: String): Seq[String] = {
    val matcher = buildMatcher(s, regex)
    val list = new scala.collection.mutable.ListBuffer[String]

    //    				def loop(i: Int): ListBuffer[String] =
    //    				if (matcher.find()) {
    //    					list.append(matcher.group(i))
    //    					loop(i + 1)
    //    				} else
    //    					list
    //    					
    //    		loop(0)

    var i: constant.JInt = 0
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
  def isDigit(s: String) = independent.invokeNonNull(s)(() => s.matches("[0-9]{1,}"))

  /**
   * 提取数字  
   */
  def extractNumbers(s: String): Seq[Int] = extractMatchedMultiple(s, "\\d+").map(_ toInt)

  /**
   * 提取非数字  
   */
  def extractNonNumbers(s: String) = extractMatchedMultiple(s, "\\D+")
}

/**
 * 常量
 */
package constant {
  
  object original {
    
    private[packages] type A     = Any
    private[packages] type R     = AnyRef
                      type SA    = scala.annotation.StaticAnnotation
                      type CT[T] = scala.reflect.ClassTag[T]
                      type TT[T] = TypeTag[T]
  }
}

package object constant {
  
  val $e = ""        // empty
  val $s = " "       // space
  val $p = '.'       // point
  val $n = null      // null
  val $u = "_"       // underline

  // --------------------------- Java Type -------------------------------
  import java.{ lang => Java, util => JUtil }
 
  type JInt                = Java.Integer
  type JLong               = Java.Long
  type JDouble             = Java.Double
  type JBoolean            = Java.Boolean
  type JTimestamp          = java.sql.Timestamp
  type JSimpleDateFormat   = java.text.SimpleDateFormat
  type JDate               = JUtil.Date

  //  Java Collection
  type JIterable[T]        = Java.Iterable[T]
  type JCollection[T]      = JUtil.Collection[T]
  type JList[T]            = JUtil.List[T]
  type JSet[T]             = JUtil.Set[T]
	type JMap[P, V]          = JUtil.Map[P, V]
  type JProperties         = JUtil.Properties
  
  /** Mapping relations
   *  
   * scala.collection.Iterable 										<=> java.lang.Iterable
   * scala.collection.Iterable 										<=> java.util.Collection
   * scala.collection.Iterator 										<=> java.util.{ Iterator, Enumeration }
   * scala.collection.mutable.Buffer  						<=> java.util.List
   * scala.collection.mutable.Set 								<=> java.util.Set
   * scala.collection.mutable.Map 								<=> java.util.{ Map, Dictionary }
   * scala.collection.mutable.ConcurrentMap 			<=> java.util.concurrent.ConcurrentMap
   * scala.collection.Seq  											 	 => java.util.List
   * scala.collection.mutable.Seq 							 	 => java.util.List
   * scala.collection.Set  											   => java.util.Set
   * scala.collection.Map 											 	 => java.util.Map
   * java.util.Properties 											 	 => scala.collection.mutable.Map[String, String]
   * 
   * Sample:
   * val scalaList = scala.collection.JavaConversions.asScalaBuffer(javaList)
   */
  // --------------------------- Java Type -------------------------------

}

