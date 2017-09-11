package persistence

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ Future, ExecutionContext }
import sojo.Account
import java.sql.Timestamp



/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-11 下午3:03:27
 * @Description 
 */
@Singleton
class AccountPersistence @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  
  import dbConfig._
  import profile.api._
  
//  private class TAccount(tag: Tag) extends Table[Account](tag, "t_account") {
//    def id       = column[Long]("id", O.PrimaryKey, O.AutoInc)
//    def username = column[String]("username")
//    def password = column[String]("password")
//    def mail     = column[String]("mail")
//    def type_    = column[String]("type")
//    def status   = column[String]("status")
//    def timestamp      = column[Timestamp]("timestamp")
//    
//  }
  
}

