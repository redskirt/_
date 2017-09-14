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

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Description 
 */

@RunWith(classOf[JUnitRunner])  
class StaticSpec extends FunSuite with Mockito with BeforeAndAfter {
  
  var _app_ :Application = _
  before {
    //    	val accountRepository = mock[AccountRepository]
    //	    val accountService = new AccountService(accountRepository)
    
    lazy val env = Environment(new java.io.File("."), this.getClass.getClassLoader, Mode.Dev)
    lazy val context = ApplicationLoader.createContext(env)
    lazy val loader = ApplicationLoader(context)
    _app_ = loader.load(context)
    Play.start(_app_)
  }
  
  test("An empty Set should have size 0") {
    assert(Set.empty.size == 1)
  }
    
  test("insertAccount") {
    	val accountService : AccountService = Application.instanceCache[AccountService].apply(_app_)
    	val account = Account("username", "password")._mail("email")._status(0)._typee(0)
	    val r = Await.result(accountService.insertAccount(account), 5.second)
	    assert(r == 1)
  }
  
  test("Account construcor") {
  
  }
  
  after(Play.stop(_app_))
  
}
