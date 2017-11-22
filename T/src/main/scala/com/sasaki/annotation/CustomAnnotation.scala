package com.sasaki.annotation

import scala.annotation.StaticAnnotation
import scala.reflect.runtime.universe._

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-10-31 下午6:10:54
 * @Description 
 */
final class Ignore extends StaticAnnotation 

final class Primary extends StaticAnnotation

final class Multiple extends StaticAnnotation

final class InstanceBody(standardSample: String) extends StaticAnnotation

final class Inject(statement: String) extends StaticAnnotation

final class AttributeMapping(namespace: String, aliase: String) extends StaticAnnotation

@InstanceBody("aaaaaaaaaaaaaaa")
case class ATest(
  @Ignore
  @AttributeMapping("namespace__", "aliase__" )
  verify__I: String, 
  @Ignore
  name: String,
  age: Int
) 

object AnnotationInterpreter {
  import independent._
  
  def main(args: Array[String]): Unit = {
//   val a = ATest("a", "a", 3)
    
    /**
     * class 反射
     */
//    val `type`: Type = typeOf[ATest]
//    val symbol: Symbol = `type`.decl(TermName("verify__I"))
//    val annotation: Annotation = symbol.annotations.head
//    val tree: Tree = annotation.tree
    
    /**
     * case class 反射 
     */
    println{
      symbolOf[ATest].asClass.annotations
    }
    
    val symbol: Symbol = symbolOf[ATest].asClass.primaryConstructor
//    println(symbol) // constructor ATest
//    println(symbol.annotations) // List()
    
    val `type`: Type = symbol.typeSignature
//    println(`type`) // (verify__I: String, name: String, age: scala.Int)org.sh.sbdp.slap.jp.annotation.ATest
//    `type`.dealias.foreach(o => println("type -> " + o))
    
    val paramLists: List[List[Symbol]] = `type`.paramLists
//    println(paramLists) // List(List(value verify__I, value name, value age))
    
//    paramLists.head.foreach(o => println(o.annotations))
//    List(org.sh.sbdp.slap.jp.annotation.Ignore, org.sh.sbdp.slap.jp.annotation.AttributeMapping("a", annotation.this.AttributeMapping.<init>$default$2))
//    List(org.sh.sbdp.slap.jp.annotation.Ignore)
//    List()
    
    val annotations = paramLists.head.head.annotations
//    println(annotations.find(_.tree.tpe =:= typeOf[AttributeMapping])) // List(org.sh.sbdp.slap.jp.annotation.AttributeMapping("a", 33))
//    println(annotations.exists(_.tree.tpe =:= typeOf[AttributeMapping]))
    val attach = annotations.find(_.tree.tpe =:= typeOf[AttributeMapping]).getOrElse(null).tree
    val Apply(_, Literal(Constant(namespace: String)) :: Literal(Constant(aliase: String)) :: Nil) = attach
//    println(s"Annotation args: name -> $namespace, num -> $aliase")
    
//    extractFieldNames[ATest] foreach println
    
    val tree = annotations.head.tree
    
//    println(showRaw(tree)) //打印语法树
    
  }
}

