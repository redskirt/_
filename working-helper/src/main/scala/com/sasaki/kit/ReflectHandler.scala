package com.sasaki.kit

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2018-01-09 上午10:53:44
 * @Description 反射基类对象属性、行为
 */
trait ReflectHandler {
  
  import scala.reflect.runtime.universe._

  lazy val mirror = runtimeMirror(getClass.getClassLoader)

  def getSuccessorName = mirror.classSymbol(getClass).fullName

  def getSuccessorPrimaryConstructorArgs = {
    val currentType = mirror.classSymbol(getClass).toType
    val instanceMirror = mirror.reflect(this)
    val constructor = currentType.decl(termNames.CONSTRUCTOR).asMethod
    val constructorParamsName = constructor.paramLists.flatten.map(_.name.toString)
    val terms = constructorParamsName.map(name => currentType.decl(TermName(name)))
    val args = terms.map(term => instanceMirror.reflectField(term.asTerm).get)
    args
  }
}

class ReflectHandlerImpl(val a: String, val b: Int) extends ReflectHandler

object Main extends App {
  
  val rh = new ReflectHandlerImpl("test", 111)
  println(rh.getSuccessorName)
  rh.getSuccessorPrimaryConstructorArgs foreach println
  
  println(this.getClass.getClassLoader.getResource(""))
}