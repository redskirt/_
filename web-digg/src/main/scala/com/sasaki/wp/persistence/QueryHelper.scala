package com.sasaki.wp.persistence

import java.sql.DriverManager
import org.squeryl.{Schema, Session, SessionFactory, Table}
import org.squeryl.adapters.MySQLAdapter
import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import com.sasaki.wp.util.Util



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
  
}

object WebDiggSchema extends Schema {
  val t_account = table[Account]("t_account") 
}

import org.squeryl.annotations.{Column, ColumnBase, Transient}
import org.squeryl.Table
import org.squeryl.Table


class Base {
  @Column("id")
  var id: Long = _
}

class Account(val account: String, val password: String) extends Base {
	var status: String = _
}

class Cookie extends Base {
  
}

object Sample extends App {
  import com.sasaki.wp.persistence.WebDiggSchema._
  
  val sf = new SessionFactory { def newSession: Session = QueryHelper.getSession().get() }
  
  import scala.io.Source
  
  val accounts: List[Account] = Source.fromFile("""H:\_\a.scala""")
    .getLines()
    .map {__ =>
      val account = new Account(__.split(':')(0), __.split(':')(1))  
      account.status_=("0")
      account
    } // foreach println
    .toList
  
  inTransaction(sf) {
	  t_account.insert(accounts);
  }
}

