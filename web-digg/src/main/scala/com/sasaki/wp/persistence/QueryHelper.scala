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
  
  def saveYenching(yenching: Yenching) = inTransaction(sf)(WebDiggYenChing.attr_harvard_yenching.insert(yenching))
  
  def saveWeiChat(o: WeiChat) = inTransaction(sf)(WebDiggWeiChat.attr_weichat.insert(o))
  
  
  // --------------------------------------  以上是WebDigg项目的查询接口，以下为VirtualSH  -------------------------------
  
  import com.sasaki.wp.persistence.VshSchema._
  
  def saveSource(source: Source) = inTransaction(sf) {
    vsh_source.insert(source);
  }
  
  def listPageId(city: String, `type`: String) = inTransaction(sf)(from(vsh_source)(o => where(o.city === city and o.`type` === `type`) select(o.pageId)).toArray)
  
  def listContent(city: String, `type`: String) = inTransaction(sf)(from(vsh_source)(o => where(o.city === city and o.`type` === `type`) select(o.pageId, o.content, o.imageId)).toArray)
  
  def updateSource(source: Source) = inTransaction(sf) {
    update(vsh_source)(o =>
      where(source.pageId === o.pageId and source.city === o.city and source.`type` === o.`type`)
        set (o.imageId := source.imageId/*o.content := source.content*/)
    )
  }
  
  def saveView(view: View) = inTransaction(sf)(vsh_view.insert(view))
		  
  def saveBook(o: Book) = inTransaction(sf)(TableBook.attr_book.insert(o))
  
  def saveBookDangDangBestseller(o: BookBestseller) = inTransaction(sf)(TableBook.attr_book_bestseller.insert(o))
  
  def updateBookInfo(book: BookBestseller) = inTransaction(sf) {
    update(TableBook.attr_book_bestseller)(o =>
      where(o.id === book.id)
        set (o.isbn := book.isbn, o.category := book.category))
  }
  
  def updateBookInfoJD(book: BookBestseller) = inTransaction(sf) {
	  update(TableBook.attr_book_bestseller)(o =>
	  where(o.id === book.id)
	  set (o.price_current := book.price_current, o.price_original := book.price_original, o.discount := book.discount))
  }

  def listBookInfo = inTransaction(sf) {
    from(TableBook.attr_book_bestseller) (o =>
      where(o.source === "dd" and o.isbn.isNull) //
      select(o.id, o.url_item)
    ).toArray
  }
  
  def listBookJDItem = inTransaction(sf) {
    from(TableBook.attr_book_bestseller)(o =>
      where(o.source === "jd") //
        select (o.id, o.url_item)).toList
  }
  
  
  
  def saveBookGrid(o: BookGrid) = inTransaction(sf)(TableBook.attr_book_grid.insert(o))
  
  def countBook = inTransaction(sf) {
    from(TableBook.attr_book)(o => compute(count)).toLong
  } 
  
  def queryMaxBookId: Long = inTransaction(sf) {
    from(TableBook.attr_book)(o =>
      compute(max(o.id)) //
    ).getOrElse(0L).toLong
  }
  
  def queryMaxBatch: Int = inTransaction(sf) {
    from(TableBook.attr_book)(o =>
      compute(max(o.batch)) //
    ).getOrElse(0).toInt
  }
  
  def saveBristol(view: Bristol) = inTransaction(sf)(attr_bristol.insert(view))
  
  def saveViewMap(map: ViewMap) = inTransaction(sf)(vsh_view_map.insert(map))
  
  def saveJoseph(o: Joseph) = inTransaction(sf)(WebDiggJoseph.attr_joseph.insert(o))
}

object WebDiggWeiChat extends Schema {
  val attr_weichat = table[WeiChat]("attr_weichat")
}

object TableBook extends Schema {
	val attr_book = table[Book]("attr_book")
	val attr_book_grid = table[BookGrid]("attr_book_grid")
	val attr_book_bestseller = table[BookBestseller]("attr_book_bestseller")
	
}

object WebDiggYenChing extends Schema {
	val attr_harvard_yenching = table[Yenching]("attr_harvard_yenching")
}

object WebDiggJoseph extends Schema {
  val attr_joseph = table[Joseph]("attr_joseph")
}

object WebDiggSchema extends Schema {
  val t_account = table[Account]("t_account")
  val t_metadata = table[Metadata]("t_metadata")
}

object VshSchema extends Schema {
  val vsh_source = table[Source]("vsh_source")
  val vsh_view = table[View]("vsh_view")
  val attr_bristol = table[Bristol]("attr_bristol")
  val vsh_view_map = table[ViewMap]("vsh_view_map")
  
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
  
  var source = Source(100077, "SHI", "map")
  source.content = "32_____4"
  updateSource(source)
  
//  println(listContent("BJG").size)
//  saveSource(source)
  
//  var viewMap = new ViewMap
//  viewMap.authors = "123"
//  saveViewMap(viewMap)
}