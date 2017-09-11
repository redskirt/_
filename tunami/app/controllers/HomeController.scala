package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-11 下午3:03:27
 * @Description 
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

}
