package repositories

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import repositories.poso.Account
import slick.jdbc.JdbcProfile
import slick.lifted.CanBeQueryCondition

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-11 下午3:03:27
 * @Description
 */
//@Singleton
class AccountRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends AbstractRepository[Account, AccountRepository.TAccount] {
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]

  import AccountRepository._
  import dbConfig._
  import profile.api._

  //  def create(username: String, password: String): Future[Account] = db.run {
  //    (t_account.map(__ => (__.username, __.password))
  //      returning(t_account.map(_.id))  
  //      into((k, v) => Account(k._1, k._2))
  //    ) += (username, password)
  //  }

  override def list(status_$active: Int = 0): Future[Seq[Account]] =
    db.run(
      t_account.filter(_.status === status_$active)
        .map(o => (o.username, o.password, o.mail, o.typee, o.status, o.timestamp)).result).map(_.map(__ => Account.apply(__._1, __._2)
        ._mail(__._3)._typee(__._4)._status(__._5)._timestamp(__._6)))

  def findByFilter[C: CanBeQueryCondition](f: (TAccount) => C): Future[Seq[Account]] = {
    db.run(t_account.withFilter(f).result)
  }

  def queryBy[C: CanBeQueryCondition](f_x: TAccount => C): Future[Account] = {
    db.run {
      t_account.withFilter(f_x).result.head
    }
  }

  def insert(a: Account): Future[Int] = db.run {
    (t_account.map { o => (o.username, o.password, o.mail, o.typee, o.status, o.timestamp) }) += (a.username, a.password, a.mail, a.typee, a.status, a.timestamp)
  }
}

object AccountRepository extends RepositoryUtil {
  import dbConfig.driver.api._

  class TAccount(tag: Tag) extends SuperTable[Account](tag, "") {
    def username = column[String]("username")
    def password = column[String]("password")
    def mail = column[String]("mail")
    def typee = column[Int]("type")
    def status = column[Int]("status")

    def * = (username, password) <> ((Account.apply _).tupled, Account.unapply)
  }
  lazy val t_account = TableQuery[TAccount]
}
