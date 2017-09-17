package repositories

import org.junit.runner._
import org.scalatest.Suite
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.specs2.mock.Mockito
import services.AccountService
import poso.Account
import scala.concurrent.Await
import org.scalatest.BeforeAndAfter
import play.api._
import scala.concurrent.duration.DurationInt
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.sql.Timestamp
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag
import slick.lifted.CanBeQueryCondition
import scala.concurrent.Future

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Description 
 */

@RunWith(classOf[JUnitRunner])  
class StaticSpec extends FunSuite with Mockito with BeforeAndAfter {

  var _app_ :Application = _
//  var dbConfigProvider: DatabaseConfigProvider = _
  before {
    //    	val accountRepository = mock[AccountRepository]
    //	    val accountService = new AccountService(accountRepository)
    
    val env = Environment(new java.io.File("."), this.getClass.getClassLoader, Mode.Dev)
    val context = ApplicationLoader.createContext(env)
    val loader = ApplicationLoader(context)
    _app_ = loader.load(context)
    Play.start(_app_)
  }
  
  lazy val dbConfigProvider = Application.instanceCache[DatabaseConfigProvider].apply(_app_)
  lazy val dbConfig = dbConfigProvider.get[JdbcProfile]
	import dbConfig._
	import dbConfig.profile.api._
  
  private class TAccount(tag: Tag) extends Table[Account](tag, "t_account") {
    def id           = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def username     = column[String]("username")
    def password     = column[String]("password")
    def mail         = column[String]("mail")
    def typee        = column[Int]("type")
    def status       = column[Int]("status")
    def timestamp    = column[Timestamp]("timestamp")
    
    def * = (username, password) <> ((Account.apply _).tupled, Account.unapply)
  }
  
  private lazy val t_account = TableQuery[TAccount]
  
  test("An empty Set should have size 0") {
    assert(Set.empty.size == 0)
  }
    
  test("insertAccount") {
    	val accountService : AccountService = Application.instanceCache[AccountService].apply(_app_)
    	val account = Account("username", "password")._mail("email")._status(0)._typee(0)
	    val r = Await.result(accountService.createAccount(account), 5.second)
	    assert(r == 1)
  }

  test("list filter account") {
    import scala.concurrent.ExecutionContext.Implicits.global
    val accounts = db.run {
      val query = t_account
        .filter(_.status === 0)
        .map(o => (o.username, o.password, o.mail))
        .result
      println(query.statements)
      query
    }
//    implicit val tag = ClassTag.asInstanceOf[Account]
    val a = accounts.map(o => o.map(o => Account.apply(o._1, o._2)._mail(o._3)))
    Await.result(a, 5.second).foreach(o => println(o.password + " " + o.mail))
  }

  test("query single by Condition") {
    def queryBy[C: CanBeQueryCondition](f_x: TAccount => C): Future[Account] = {
      db.run { t_account.withFilter(f_x).result.head }
    }
    val a = queryBy { o => o.status === 0 }
    println(Await.result(a, 5.second).status)
    
  }
  
  after(Play.stop(_app_))
  
}