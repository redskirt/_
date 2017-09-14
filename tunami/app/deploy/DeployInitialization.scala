package deploy

import play.api._
import javax.inject.Inject
import services.AccountService
import poso.Account
import scala.concurrent.Await

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-14 下午4:47:38
 * @Description
 */

class DeployInitialization(mode: Mode = Mode.Dev) {
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

  lazy val accountService: AccountService = Application.instanceCache[AccountService].apply(_app_)

  def init(implicit f_x: Application => Unit) = {
    import scala.concurrent.duration.DurationInt
    val admin = Account("Sasaki", "redskirt_")._mail("redskirt@outlook.com")
    val init_admin = Await.result(accountService.insertAccount(admin), 5.second)
    assert(init_admin == 1, "init_admin fail!")
    
    f_x
  }
}

object DeployInitialization {
  implicit val handler = (app: Application) => {
//    if (null != app)
    Play.stop(app)
    Logger.info(" ------------ Application have stoped! ------------ ")
  }

  def main(args: Array[String]): Unit = {
    new DeployInitialization(Mode.Dev).init(handler)
  }
}
