package controllers

import javax.inject.Inject
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import javax.inject.Singleton
import play.api.mvc.Request
import play.api.mvc.AnyContent

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-06 下午5:52:56
 * @Description 
 */
@Singleton
class LoginController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  
  def login() = Action { implicit request: Request[AnyContent] =>
    println("login --> " + request.uri)
    Ok(views.html.login())
  }
  
  
}