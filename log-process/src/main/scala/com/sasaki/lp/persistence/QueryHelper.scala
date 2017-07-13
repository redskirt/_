package com.sasaki.lp.persistence

import java.sql.DriverManager
import com.sasaki.lp.util.Util
import com.sasaki.lp.poso.Base
import org.squeryl.{Schema, Session, SessionFactory, Table}
import org.squeryl.adapters.MySQLAdapter
import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._

object QueryHelper {
  
  def getSession(): Option[() => Session] = {
    import org.squeryl.SessionFactory
    if (SessionFactory.concreteFactory.isEmpty && Util.hasConstants("jdbc.url", "jdbc.username", "jdbc.password")) {
      Class.forName("oracle.jdbc.OracleDriver")

      Some(() => {
        val connector = DriverManager.getConnection(
          Util.prop("jdbc.url"),
          Util.prop("jdbc.username"),
          Util.prop("jdbc.password"))
        connector.setAutoCommit(true)
        Session.create(connector, new MySQLAdapter)
      })
    } else None
  }
 
  def list(t: Table[_<: Base]) = t.seq
  def queryById(id: Long, t: Table[_<: Base]) = from(t)(__ => where(__.taskId === id) select(__)).single
  
}

object LppSchema extends Schema {
  import com.sasaki.lp.poso._
  
  val area_top3_product = table[AreaTop3Product]("area_top3_product") 
  val page_split_convert_rate = table[PageSplitConvertRate]("page_split_convert_rate") 
  val session_aggregation_status = table[SessionAggregationStatus]("session_aggregation_status") 
  val session_detail = table[SessionDetail]("session_detail") 
  val session_random_extract = table[SessionRandomExtract]("session_random_extract") 
  val task = table[Task]("task") 
  val top10_category = table[Top10Category]("top10_category") 
  val top10_sessio = table[Top10Session]("top10_session")
  
}