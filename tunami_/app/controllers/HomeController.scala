package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import services.AccountService
import scala.concurrent.Await
import play.api.libs.json.Json
import scala.concurrent.duration.Duration

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-11 下午3:03:27
 * @Description 
 */
@Singleton
class HomeController @Inject()(accountService: AccountService, cc: ControllerComponents) extends AbstractController(cc) {

  
  def index() = Action { implicit request: Request[AnyContent] =>
    val a = Await.result(accountService.queryAll(), Duration.Inf)
     Ok(views.html.test(a))
  }

}
