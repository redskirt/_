package poso

import scala.reflect.runtime.universe._
import independent._


/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-11 上午11:38:41
 * @Description 
 */

class Super[T] {
  var id: Int = _
  var timestamp: java.sql.Timestamp = new java.sql.Timestamp(System.currentTimeMillis())

  def _id(id: Int) = { this.id = id; this}
  
  // TODO: 实现Scala反射，设置属性方法
  def set(t: T, attr: String, $attr: AnyRef): T = {
    require(isEmpty(t) || isEmpty(attr) || isEmpty($attr), "Entity[T]/attr/$attr_is Null.") 
    t.getClass().getMethod("", $attr.getClass()).invoke(attr, $attr)
    // TODO : ???
    t
  }
  
  def setMult(t: T, attrs_$attrs: Array[Array[Any]]): T = ???
  
  implicit class TypeDetector[T: TypeTag](related: Super[T]) {
    def getType(): Type = typeOf[T]
  }
}

  
case class Account(val username: String, val password: String) extends Super[Account]{
    var mail: String = _
    var typee : Int = _ // admin -> 0, user -> 1
    var status: Int = _ // enable -> 0, lock -> 1, delete -> 2
    
    def _mail(mail: String) = { this.mail = mail; this}
    def _typee(typee: Int) = { this.typee = typee; this}
    def _status(status: Int) = { this.status = status; this} 
}


  