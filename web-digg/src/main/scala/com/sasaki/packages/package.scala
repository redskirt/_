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

  def invokeVerify[T](condition: Boolean, slogan: String)(g_x: () => T) = {
    require(condition, slogan)
    g_x()
  }

  def invokeVerify[T](f_x: () => Boolean, slogan: String)(g_x: () => T) = {
    require(f_x(), slogan)
    g_x()
  }

  @deprecated("该函数编译时非类型安全，慎用。")
  def invokeNonNothing[E: TT, T /*Which return type of function*/ ](g_x: () => T) =
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
   * ab && ab && ba     -> true
   * ab && b            -> false
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
    case _                    => println(s"Invalid json --> $json"); false
  }

  final val TIME_MULLIONS = /*java.time.Instant.now().toEpochMilli()*/
    java.time.Clock.systemUTC().millis()
  final val TODAY = new JDate(TIME_MULLIONS)

  final object TimePattern extends Enumeration {
    type TimePattern = Value
    val PATTERN_DATE = Value("yyyy-MM-dd")
    val PATTERN_TIMESTAMP = Value("yyyy-MM-dd hh:mm:ss")

    def name(o: TimePattern.Value) = o.toString()
  }

  /**
   * // TODO 方法未测试！
   */
  import TimePattern._
  def timestamp(string: String, pattern: TimePattern): Option[JTimestamp] =
    pattern match {
      case PATTERN_DATE      => Some(new JTimestamp(new JSimpleDateFormat(PATTERN_DATE.toString()).parse(string).getTime))
      case PATTERN_TIMESTAMP => Some(new JTimestamp(new JSimpleDateFormat(PATTERN_TIMESTAMP.toString()).parse(string).getTime))
    }

  val currentFormatedDatetime: String =
    new JSimpleDateFormat(PATTERN_TIMESTAMP.toString()).format(TODAY)

  val currentFormatedDate: String =
    new JSimpleDateFormat(PATTERN_DATE.toString()).format(TODAY)

  def computeDate(date: JDate, offset: Int): JDate = {
    import java.util.Calendar

    val calendar = Calendar.getInstance()
    calendar.setTime(date)
    calendar.add(Calendar.DAY_OF_MONTH, offset)
    new JDate(calendar.getTimeInMillis)
  }

  def yesterday(date: JDate) = computeDate(date, -1)

  def tomorrow(date: JDate) = computeDate(date, 1)

  //  def formatDuration(durationTimeMillis: Long) =
  //    org.apache.commons.lang3.time.DurationFormatUtils.formatDuration(durationTimeMillis, "HH:mm:ss", true)
  //
  //  def formatUntilDuration(lastTimeMillis: Long) = formatDuration(currentTimeMillis - lastTimeMillis)

  import java.io._
  import java.util.zip.{ ZipEntry, ZipOutputStream }
  
  /**
   * File Operation
   */
  def listFiles(url: String) = {
    import java.io.File

    val t = new File(url)
    if (t.isDirectory())
      t.listFiles().filterNot(_.isDirectory())
    else
      Array(t)
  }

  /**
   * @param fileNameWithPath 文件全路径
   * @param content
   */
  def writeFile(fileNameWithPath: String, content: String) = {
    import java.io.{ File, FileWriter, BufferedWriter }

    val writer = new BufferedWriter(new FileWriter(new File(fileNameWithPath)))
    writer.write(content)
    writer.close()
  }

  /**
   * 压缩列表中的文件
   */
  def zip(files: Seq[File], zipFileNameWithAbsolutePath: String): Unit =
    invokeVerify(files.forall(o => o.exists() && o.isFile()), "文件不存在或存在目录文件。") { () =>

      val bufferSize = 2 * 1024 * 1024
      val buffer = new Array[Byte](bufferSize)
      val zipFile = new File(zipFileNameWithAbsolutePath)
      val outputStream = new FileOutputStream(zipFile)
      val zipOutputStream = new ZipOutputStream(outputStream)

      var fileInputStream: FileInputStream = null
      var bufferedInputStream: BufferedInputStream = null

      try {
        files.foreach { o =>
          fileInputStream = new FileInputStream(o)
          bufferedInputStream = new BufferedInputStream(fileInputStream, bufferSize)
          zipOutputStream.putNextEntry(new ZipEntry(o.getName))

          var i = 0
          while ({
            i = fileInputStream.read(buffer, 0, bufferSize)
            i != -1
          }) {
            zipOutputStream.write(buffer, 0, i)
          }
        }
      } finally {
        if (null != fileInputStream)
          fileInputStream.close()
        if (null != bufferedInputStream)
          bufferedInputStream.close()

        zipOutputStream.close()
        outputStream.close()
      }
    }

  /**
   * 压缩单个文件
   */
  def zip(file: File, zipFileNameWithAbsolutePath: String): Unit =
     zip(Seq(file), zipFileNameWithAbsolutePath)  
     
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
   * 示例：
   * H:/git-repo/_/working-helper/target/classes/
   */
  def classpath =
    independent.erase(getClass.getClassLoader.getResource($e).toString, "file:/")

  def symbol[T: TT]: ClassSymbol =
    symbolOf[T].asClass

  /**
   * 由 Java Reflect 获得泛型的类型 Class[T]
   * 适用于 abstract class 或 trait
   */
  def extractTypeClass[T: TT](self: AnyRef): Class[T] =
    self.getClass()
      .getGenericSuperclass()
      .asInstanceOf[java.lang.reflect.ParameterizedType]
      .getActualTypeArguments.apply(0)
      .asInstanceOf[Class[T]]

  @deprecated
  def buildInstance[T: TT](args: A*): T = ???
  //    runtimeMirror(getClass.getClassLoader)
  //      .reflectClass(clazz[T])
  //      .reflectConstructor(extractConstructor[T])
  //      .apply(args: _*)
  //      .asInstanceOf[T]

  def extractConstructor[T: TT]: MethodSymbol =
    typeOf[T].decl(termNames.CONSTRUCTOR).asMethod

  def typeNothing[T: TT]: Boolean =
    typeEqual[T, Nothing]

  def typeIs[T: TT](t: Type): Boolean =
    t =:= typeOf[T]

  def typeEqual[E: TT, T: TT]: Boolean =
    typeOf[E] =:= typeOf[T]

  def typeFrom[E: TT, T: TT]: Boolean =
    typeOf[E] <:< typeOf[T]

  /**
   * 仅适用case class
   */
  @deprecated
  def extractFields[T: TT]: Seq[String] = ???
  //    typeOf[T].members.collect {
  //      case m: MethodSymbol if m.isCaseAccessor => m.name.toString()
  //    }.toList.reverse

  /**
   * Java 反射获取属性列表
   * 参数 classOf[T]
   */
  def extractFields[T: TT](clazz: Class[T]): Seq[Field] =
    clazz.getDeclaredFields.map { o => o.setAccessible(true); o }

  /**
   * Java 反射获取父类属性列表
   * 参数 classOf[T]
   */
  def extractFieldsSuccessor[T: TT](clazz: Class[T]): Seq[Field] =
    clazz.getSuperclass.getDeclaredFields.map { o => o.setAccessible(true); o }

  /**
   * Java 反射获取当前类和父类属性列表
   * 参数 classOf[T]
   */
  def extractFieldsAll[T: TT](clazz: Class[T]): Seq[Field] =
    extractFieldsSuccessor(clazz) union extractFields(clazz)

  def extractSimpleName[T: TT](t: Class[T]): String =
    t.getSimpleName

  def extractSimpleName[T: TT]: String =
    extractFullName[T].split($p).last

  def extractFullName[T: TT](t: Class[T]): String =
    t.getName

  def extractFullName[T: TT]: String =
    typeOf[T].toString()

  @deprecated
  def extractTypes[T: TT]: Seq[Type] =
    extractSymbolList[T].head.map(_ typeSignature)

  @deprecated
  private def extractSymbolList[T: TT]: Seq[Seq[Symbol]] = ???
  //    symbolOf[T].asClass.primaryConstructor.typeSignature.paramLists

  /**
   * 类标识注解
   * for case class / normal class
   *
   * 以下项测试通过
   *
   * @transient
   * class O(@transient id: Int)
   *
   * @transient
   * case class O(@transient id: Int)
   *
   * 结果
   * List(scala.transient)
   */
  def extractClassAnnotations[T: TT]: Seq[Annotation] =
    typeOf[T].typeSymbol.annotations

  /**
   * 字段标识注解
   * for case class / normal class
   *
   * 以下项测试通过
   *
   * @transient
   * class O(@transient id: Int) {
   *
   *   @BeanProperty
   *   @transient
   *   @throws
   *   var attr1 : String = _
   *
   *   @transient
   *   var attr2 : String = _
   * }
   *
   * @transient
   * case class O(@transient id: Int) {
   *
   *   @BeanProperty
   *   @transient
   *   @throws
   *   var attr1 : String = _
   *
   *   @transient
   *   var attr2 : String = _
   * }
   *
   * 结果
   * normal class:
   * ArrayBuffer(
   * List(scala.beans.BeanProperty, scala.transient, scala.throws[scala.Nothing](scala.this.throws.<init>$default$1[scala.Nothing])),
   * List(scala.transient)
   * )
   *
   * case class:
   * ArrayBuffer(
   * List(scala.transient),
   * List(scala.beans.BeanProperty, scala.transient, scala.throws[scala.Nothing](scala.this.throws.<init>$default$1[scala.Nothing])),
   * List(scala.transient)
   * )
   *
   * 注意：normal class 不能获得主构造方法中的值！
   */
  def extractFieldAnnotations[T: TT](clazz: Class[T]): Seq[Seq[Annotation]] =
    extractFields(clazz).map { o => typeOf[T].decl(TermName(s"${o.getName} ")).annotations }

  /**
   * 字段标识联合注解
   * for case class / normal class
   *
   * 测试项以 extractFieldAnnotations 为准
   * @see extractFieldAnnotations[T: TT](clazz: Class[T]): Seq[Seq[Annotation]]
   *
   */
  def extractField2Annotations[T: TT](clazz: Class[T]): Seq[(String, Seq[Annotation])] =
    extractFields(clazz).map(_.getName) zip extractFieldAnnotations(clazz)

  @deprecated
  def extractField2Type[T: TT]: Seq[(String, Type)] =
    extractFields[T] zip extractTypes[T]

  //  def extractSingleFieldWhileAnnotation[T: TT, E <: SA: TT]: Option[String] =
  //    extractField2Annotations[T]
  //      .find(_._2.exists(o => typeIs[E](o.tree.tpe)))
  //      .map(_._1)

  //  def extractListFieldWhileAnnotation[T: TT, E <: SA: TT] = ???

  def existsAnnotationFromType[T: TT, E <: SA: TT]: Boolean =
    extractClassAnnotations[T]
      .exists(o => typeIs[E](o.tree.tpe))

  /**
   * 判断字段是否存在该注解
   * or case class / normal class
   *
   * 测试项以 extractFieldAnnotations 为准
   * @see extractFieldAnnotations[T: TT](clazz: Class[T]): Seq[Seq[Annotation]]
   *
   * 注意：对 normal class 使用时必须确保目标字段不属于主构造方法，否则有误！原因，参考 @see 测试项说明。
   */
  def existsAnnotationFromField[T: TT, E <: SA: TT](clazz: Class[T], field: String): Boolean = {
    val opField___Annotations = extractField2Annotations(clazz).find(_._1 == field)
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

    //            def loop(i: Int): ListBuffer[String] =
    //            if (matcher.find()) {
    //              list.append(matcher.group(i))
    //              loop(i + 1)
    //            } else
    //              list
    //
    //        loop(0)

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

    private[packages] type A = Any
    private[packages] type R = AnyRef
    type SA = scala.annotation.StaticAnnotation
    type CT[T] = scala.reflect.ClassTag[T]
    type TT[T] = TypeTag[T]

  }
}

package object constant {

  val $e = ""       // empty
  val $s = " "      // space
  val $p = '.'      // point
  val $n = null     // null
  val $u = "_"      // underline

  // --------------------------- Java Type -------------------------------
  import java.{ lang => Java, util => JUtil, sql => JSql }

  //  type JObject             = Java.Object
  type JInt              = Java.Integer
  type JLong             = Java.Long
  type JDouble           = Java.Double
  type JBoolean          = Java.Boolean
  type JTimestamp        = JSql.Timestamp
  type JSimpleDateFormat = java.text.SimpleDateFormat
  type JDate             = JSql.Date

  //  Java Collection
  type JIterable[T]      = Java.Iterable[T]
  type JCollection[T]    = JUtil.Collection[T]
  type JList[T]          = JUtil.List[T]
  type JSet[T]           = JUtil.Set[T]
  type JMap[P, V]        = JUtil.Map[P, V]
  type JProperties       = JUtil.Properties

  // Java Reflect
  type Field = Java.reflect.Field

  /**
   * Mapping relations
   *
   * scala.collection.Iterable                     <=> java.lang.Iterable
   * scala.collection.Iterable                     <=> java.util.Collection
   * scala.collection.Iterator                     <=> java.util.{ Iterator, Enumeration }
   * scala.collection.mutable.Buffer              <=> java.util.List
   * scala.collection.mutable.Set                 <=> java.util.Set
   * scala.collection.mutable.Map                 <=> java.util.{ Map, Dictionary }
   * scala.collection.mutable.ConcurrentMap       <=> java.util.concurrent.ConcurrentMap
   * scala.collection.Seq                            => java.util.List
   * scala.collection.mutable.Seq                   => java.util.List
   * scala.collection.Set                           => java.util.Set
   * scala.collection.Map                           => java.util.Map
   * java.util.Properties                           => scala.collection.mutable.Map[String, String]
   *
   * Sample:
   * val scalaList = scala.collection.JavaConversions.asScalaBuffer(javaList)
   */
  // --------------------------- Java Type -------------------------------

}

