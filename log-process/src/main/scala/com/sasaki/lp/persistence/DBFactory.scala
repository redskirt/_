package org.sbdp.slap.lp.persistence

import org.squeryl.Session
import java.sql.DriverManager
import org.squeryl.adapters.OracleAdapter
import org.squeryl.SessionFactory
import com.sasaki.lp.util.Util

trait DBFactory {

  def getSession: Option[() => Session] = {
    
    if (!SessionFactory.concreteFactory.isEmpty && Util.hasConstants("jdbc.url", "jdbc.username", "jdbc.password")) {
      Class.forName("oracle.jdbc.OracleDriver")

      Some(() => {
        val connector = DriverManager.getConnection(
          Util.prop("jdbc.url"),
          Util.prop("jdbc.username"),
          Util.prop("jdbc.password"))
        connector.setAutoCommit(false)
        Session.create(connector, new OracleAdapter)
      })
    } else None
  }
  
  def main(args: Array[String]): Unit = {
    
  }

}