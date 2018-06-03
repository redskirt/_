package repositories

import javax.inject._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import repositories.poso.Account
import slick.jdbc.JdbcProfile


/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-11 下午3:03:27
 * @Description
 */
@Singleton
class AccountRepository /*@Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)*/ extends AbstractRepository[Account, AccountRepository.TAccount]{
//  protected val dbConfig = dbConfigProvider.get[JdbcProfile]
  import AccountRepository._
  import profile.api._
  
  override protected val q: TableQuery[TAccount] = TableQuery[TAccount]

  //  def create(username: String, password: String): Future[Account] = db.run {
  //    (t_account.map(__ => (__.username, __.password))
  //      returning(t_account.map(_.id))  
  //      into((k, v) => Account(k._1, k._2))
  //    ) += (username, password)
  //  }

//  override def list(status_$active: Int = 0): Future[Seq[Account]] =
//    db.run(
//      t_account.filter(_.status === status_$active)
//        .map(o => (o.username, o.password, o.mail, o.typee, o.status, o.timestamp)).result).map(_.map(__ => Account.apply(__._1, __._2)
//        ._mail(__._3)._typee(__._4)._status(__._5)._timestamp(__._6)))
//
//  def findByFilter[C: CanBeQueryCondition](f: (TAccount) => C): Future[Seq[Account]] = {
//    db.run(t_account.withFilter(f).result)
//  }
//
//  def queryBy[C: CanBeQueryCondition](f_x: TAccount => C): Future[Account] = {
//    db.run {
//      t_account.withFilter(f_x).result.head
//    }
//  }
//
//  def insert(a: Account): Future[Int] = db.run {
//    (t_account.map { o => (o.username, o.password, o.mail, o.typee, o.status, o.timestamp) }) += (a.username, a.password, a.mail, a.typee, a.status, a.timestamp)
//  }
}

object AccountRepository extends HasDatabaseConfig[JdbcProfile] {
  protected lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._
  import enums.Constant._

  class TAccount(tag: Tag) extends RepositoryUtil.SuperTable[Account](tag, $t_account) {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def username = column[String]("username")
    def password = column[String]("password")
    def mail = column[Option[String]]("mail")
    def typee = column[Int]("type")
    def status = column[Int]("status")

    def * = (id, username, password, mail) <> ((Account.apply _).tupled, Account.unapply)
  }
  
  lazy val t_account = TableQuery[TAccount]
  
}
