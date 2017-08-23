package com.sasaki.wp.persistence

import java.sql.DriverManager

import org.squeryl.Schema
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.MySQLAdapter
import org.squeryl.annotations.Column
import org.squeryl.PrimitiveTypeMode._

import com.sasaki.wp.util.Util
import java.sql.Timestamp

object QueryHelper {

  def getSession(): Option[() => Session] = {
    import org.squeryl.SessionFactory
    if (SessionFactory.concreteFactory.isEmpty && Util.hasConstants("db.url", "db.username", "db.password")) {
      Class.forName(Util.prop("db.driverClassName"))

      Some(() => {
        val connector = DriverManager.getConnection(
          Util.prop("db.url"),
          Util.prop("db.username"),
          Util.prop("db.password"))
        connector.setAutoCommit(true)
        Session.create(connector, new MySQLAdapter)
      })
    } else None
  }

  /**
   * 查询接口
   */
  import com.sasaki.wp.persistence.WebDiggSchema._
  implicit val sf = new SessionFactory { def newSession: Session = QueryHelper.getSession().get() }

  /**
   * 保存新元数据，带Cookie信息
   */
  def saveMetadata(metadata: Metadata) = inTransaction(sf)(t_metadata.insert(metadata))
  
  /**
   * 仅查询Cookie字符串
   */
  def queryCookie(account: String, _type: String = "init"): String = inTransaction(sf) {
    from(t_metadata)(__ => where(__.account === account and __._type === _type) select(__.cookie)).headOption.getOrElse("")
  }
  
  /**
   * 仅更新Cookie字符串
   */
  def updateCookie(metadata: Metadata) = inTransaction(sf) {
    update(t_metadata)(__ => 
      where(__.account === metadata.account and __._type === metadata._type)  
        set(__.cookie := metadata.cookie)
    )
  }
  
  def listAccount = inTransaction(sf)(from(t_account)(select(_)).toList)
  def listMetadata = inTransaction(sf)(from(t_metadata)(select(_)).toList)

}

object WebDiggSchema extends Schema {
  val t_account = table[Account]("t_account")
  val t_metadata = table[Metadata]("t_metadata")
}

class Base {
  @Column("id")
  var id: Long = _
}

case class Account(val account: String, val password: String) extends Base {
  var status: String = _
}

case class Metadata(val account: String, val cookie: String) extends Base {
  @Column("type")
  var _type: String = _
  var timestamp: Timestamp = new Timestamp(System.currentTimeMillis())
  
  def setType(_type: String): Metadata = { this._type = _type; this }
}

object Sample extends App {
  import com.sasaki.wp.persistence.WebDiggSchema._

  val sf = new SessionFactory { def newSession: Session = QueryHelper.getSession().get() }

  import scala.io.Source

//  val accounts: List[Account] = Source.fromFile("""H:\_\a.scala""")
//    .getLines()
//    .map { __ =>
//      val account = new Account(__.split(':')(0), __.split(':')(1))
//      account.status_=("0")
//      account
//    } // foreach println
//    .toList

//  inTransaction(sf) {
//    t_account.insert(accounts);
//  }
    
//    QueryHelper.saveMetadata(Metadata("t", "t").setType("type"))
//    println(QueryHelper.queryCookie("t", "type"))
//    QueryHelper.updateCookie(Metadata("t", "sssss").setType("typew"))
//    QueryHelper.listMetadata.foreach(println)
    QueryHelper.listAccount.foreach(println)
    
}

