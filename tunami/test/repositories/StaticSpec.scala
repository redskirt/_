package repositories

import java.io.File



import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite

import play.api.Application
import play.api.ApplicationLoader
import play.api.Environment
import play.api.Mode
import play.api.Play
import repositories.AccountRepository.TAccount
import repositories.poso.Account
import play.api.inject.guice.GuiceApplicationBuilder
import org.scalatest.junit.JUnitRunner


/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Description
 */

@RunWith(classOf[JUnitRunner])
class StaticSpec extends FunSuite with BeforeAndAfter {
  var _app_ : Application = _
  before {
//    val env = Environment(new java.io.File("."), this.getClass.getClassLoader, Mode.Dev)
//    val context = ApplicationLoader.createContext(env)
//    val loader = ApplicationLoader(context)
//    _app_ = loader.load(context)
//    Play.start(_app_)
  }

  test("An empty Set should have size 0") {
    assert(Set.empty.size == 0)
  }

  test("test1") {
    import play.api.inject.bind
    
    val application = new GuiceApplicationBuilder()
      .in(Environment(new File("path/to/app"), this.getClass.getClassLoader, Mode.Test))
      .bindings(new modules.Module)
//      .bindings(bind[Repository[Account, TAccount]].to[AbstractRepository[Account, TAccount]])
      .build()
      
   val dao = Application.instanceCache[Repository[TAccount, Account]].apply(application)
   println("dao ----- " + dao)     

  }

  after(Play.stop(_app_))

}