package repositories

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ Future, ExecutionContext }
import poso.Account
import java.sql.Timestamp



/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-11 下午3:03:27
 * @Description 
 */
@Singleton
class AccountRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends Repository[Account] {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  
  import dbConfig._
  import profile.api._
  
  override def list[T](): List[T] = ??? 
  
  private class TAccount(tag: Tag) extends Table[Account](tag, "t_account") {
    def id       = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def username = column[String]("username")
    def password = column[String]("password")
    def mail     = column[String]("mail")
    def typee    = column[String]("type")
    def status   = column[String]("status")
    def timestamp      = column[Timestamp]("timestamp")
    
    def * = (username, password) <> ((Account.apply _).tupled, Account.unapply)
  }
  
  private lazy val t_account = TableQuery[TAccount]
  
  def create(username: String, password: String): Future[Account] = db.run {
    (t_account.map(__ => (__.username, __.password))
      returning(t_account.map(_.id))    
      into((k, v) => Account(k._1, k._2))
    ) += (username, password)
  }
  
  def insert(a: Account): Future[Int] = db.run { t_account += a }
}

