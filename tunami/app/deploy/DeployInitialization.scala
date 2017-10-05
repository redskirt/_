package deploy

import play.api._
import javax.inject.Inject
import services.AccountService
import repositories.poso.Account
import scala.concurrent.Await

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-14 下午4:47:38
 * @Description
 */

class DeployInitialization(mode: Mode = Mode.Dev) {
  import independent._
  
  var _app_ : Application = _
  try {
    lazy val env = Environment(new java.io.File("."), this.getClass.getClassLoader, mode)
    lazy val context = ApplicationLoader.createContext(env)
    lazy val loader = ApplicationLoader(context)
    _app_ = loader.load(context)
    Play.start(_app_)
    Logger.info(" ------------ Application start completed! ------------ ")
  } catch {
    case t: Throwable => Logger.error(" ------------ Application start fail! ------------ ", t)
  }

  val accountService: AccountService = Application.instanceCache[AccountService].apply(_app_)
  
  def handler(app: Application)(f_x: () => Unit) = 
    try f_x()
    finally {
      if (null != app) Play.stop(app)
      Logger.info(" ------------ Application have stoped! ------------ ")
    }

  private def init() = {
		import scala.concurrent.duration.DurationInt
    handler(_app_) { () => 
//      val admin = Account(1, "Sasaki", md5("redskirt_"), "")/*._mail("redskirt@outlook.com")*/._status(0)._typee(0)
//      val init_admin = Await.result(accountService.createAccount(admin), 15.second)
//      assert(init_admin == 1, "init_admin fail!")
    }
  }
}

object DeployInitialization {
  def main(args: Array[String]): Unit = {
    new DeployInitialization(Mode.Dev).init()
  }
}
