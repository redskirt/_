package views

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-08 下午2:34:34
 * @Description
 */
class ViewHandler {
  
}

object ViewHandler {
  val title = "TUNAMI后台管理系统 v1.0"
  
  def $(s: String) = controllers.routes.Assets.versioned(s)
}