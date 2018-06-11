package com.sasaki.wp.persistence

import java.sql.DriverManager

import org.squeryl.Schema
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.MySQLAdapter
import org.squeryl.PrimitiveTypeMode._

import com.sasaki.wp.util.Util
import java.sql.Timestamp
import com.sasaki.wp.persistence.poso._

trait QueryHelper {

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
  implicit val sf = new SessionFactory { def newSession: Session = getSession().get() }

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
  
  
  // --------------------------------------  以上是WebDigg项目的查询接口，以下为VirtualSH  -------------------------------
  
  import com.sasaki.wp.persistence.VshSchema._
  
  def saveSource(source: Source) = inTransaction(sf) {
    vsh_source.insert(source);
  }
  
  def listPageId(city: String) = inTransaction(sf)(from(vsh_source)(o => where(o.city === city) select(o.pageId)).toArray)
  
  def listContent(city: String) = inTransaction(sf)(from(vsh_source)(o => where(o.city === city) select(o.pageId, o.content, o.imageId)).toArray)
  
  def updateSource(source: Source) = inTransaction(sf) {
    update(vsh_source)(o =>
      where(source.pageId === o.pageId and source.city === o.city)
        set ( o.content := source.content/*o.imageId := source.imageId,, o.base64Image := source.base64Image*/)
    )
  }
  
  def saveView(view: View) = inTransaction(sf)(vsh_view.insert(view))
}

object WebDiggSchema extends Schema {
  val t_account = table[Account]("t_account")
  val t_metadata = table[Metadata]("t_metadata")
}

object VshSchema extends Schema {
  val vsh_source = table[Source]("vsh_source")
  val vsh_view = table[View]("vsh_view")
}

object Sample extends QueryHelper with App {
//  import com.sasaki.wp.persistence.WebDiggSchema._
  import com.sasaki.wp.persistence.VshSchema._

  override val sf = new SessionFactory { def newSession: Session = getSession().get() }

  //  val accounts: List[Account] = scala.io.Source.fromFile("""H:\_\a.scala""")
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
  //    listAccount.foreach(println)

//  listPageId("bj").take(10).foreach(println)
  
//  var source = Source(11787, "bj")
//  source.content = "324"
//  updateSource(source)
  
  println(listContent("BJG").size)
}

