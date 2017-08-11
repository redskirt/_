package com.sasaki.wp.sample

import scala.annotation.elidable
import scala.annotation.elidable.ASSERTION

import org.apache.http.Consts
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.CookieStore
import org.apache.http.client.HttpClient
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.cookie.CookieSpecProvider
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.http.impl.cookie.DefaultCookieSpecProvider
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.HttpContext
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods
import org.json4s.jvalue2extractable
import org.json4s.jvalue2monadic
import org.json4s.string2JsonInput

import com.sasaki.wp.enums.E._
import com.sasaki.wp.persistence.Metadata
import com.sasaki.wp.persistence.QueryHelper
import com.sasaki.wp.util.Util

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-08-08 上午10:54:09
 * @Description
 */
class WebDigg {

}

object WebDigg extends App {
  
  implicit val formats = DefaultFormats
  
  implicit val cookieStore = new BasicCookieStore
  implicit val requestConfig = RequestConfig.DEFAULT
  implicit val context = HttpClientContext.create()
  implicit val registry: Registry[CookieSpecProvider] = RegistryBuilder.create[CookieSpecProvider]()
    .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider())
    .build
  implicit val client = HttpClients.custom()
    .setDefaultCookieStore(cookieStore)
    .setDefaultRequestConfig(requestConfig)
    .setDefaultCookieSpecRegistry(registry)
    .build()
  
  val cookieStr = "UM_distinctid=15db62375bf520-07e30c1eda903a-143a6d54-13c680-15db62375c0424; uuid=\"w:aa24cc220e7a418bb6e4cffa8be3c448\"; login_flag=812f9a7d1d30496c2f308e906425d8cd; sessionid=3130dab04efadc6c8c35e871de3291bd; uid_tt=bd07b45d64ce252fe3fd023283ded123; sid_tt=3130dab04efadc6c8c35e871de3291bd; sid_guard=\"3130dab04efadc6c8c35e871de3291bd|1502011604|2591999|Tue\054 05-Sep-2017 09:26:43 GMT\"; sso_login_status=1; csrftoken=5c0e8529e5ad23b900963421010af5cb; WEATHER_CITY=%E5%8C%97%E4%BA%AC; tt_webid=6451038131775948302; CNZZDATA1259612802=1700012720-1501999169-%7C1502030762; __tasessionId=o5qe4kut81502032004628"
  val postCookie = """csrftoken=099c9203c938060b4f1ea3dce16ab1a1; tt_webid=6447316118323512846; WEATHER_CITY=%E5%8C%97%E4%BA%AC; UM_distinctid=15d827c2ccf51-0963e2cc5326bc8-41554330-1fa400-15d827c2cd038c; CNZZDATA1259612802=407746919-1501128910-https%253A%252F%252Fwww.bing.com%252F%7C1502152511; uuid="w:82ca84223a28448cb7dfda2dfa5eab1c"; sso_login_status=1; login_flag=0c02740c9e36917cafaabd4768f9ec29; sessionid=23db5f93ebc0295196623c8cbf22f3d1; uid_tt=18383eb38585d5a3988fa25d7eb5fe9a; sid_tt=23db5f93ebc0295196623c8cbf22f3d1; sid_guard="23db5f93ebc0295196623c8cbf22f3d1|1502084673|2591999|Wed\054 06-Sep-2017 05:44:32 GMT"; __tasessionId=e5qz7x4j21502155726741"""

  val comment_id = "1575095098176542"
  val dongtai_id = "1575095098176542"
  val group_id = "6451469307842429198"
  val item_id = "6451472465402003981"

  
  /**
   * 登陆前，获取验证码Base64Code
   */
//  println("captchaStr --> " + getCaptchaStrService)
  val str = """
    {"showapi_res_code":0,"showapi_res_error":"","showapi_res_body":{"Result":"haue","ret_code":0,"Id":"95f4d921-78c8-41bf-bc78-48a33ca9be56"}}
  """
  
  doLoginService(DEFAULT_ACCOUNT, DEFAULT_PASSWORD, getCaptchaStrService)
  
  val testPage = doGET("http://www.toutiao.com/a6451469307842429198/#p=1")
  println(s"testPage --> $testPage")

  
  /**
   * 直接获取登陆验证码待识别字符串
   */
  def getCaptchaStrService(): String = {
    val loginContent: String = doGET(url_get_login)
    Util.getMatched(loginContent, captcha_regex)
  }
  
  /**
   * 登陆，三方验证码解析
   */
  def doLoginService(account: String, password: String, captchaStr: String) {
    // 执行登陆，返回状态码判断
    try {
      // 调用验证码识别
      var _resultCaptchaCode = invokeParseCaptcha(captchaStr)
      println("invokeParseCaptcha，三方验证码返回结果： --> " + _resultCaptchaCode)
//      var _resultCaptchaCode = getResultCaptchaCode(_requltCaptchaCodeJson)

      val response = post(url_post_account_login,
        Map(("mobile" -> "sw"), ("code" -> "ss"), ("account" -> account), ("password" -> password),
          ("captcha" -> _resultCaptchaCode), ("is_30_days_no_login" -> "false"), ("service", "https://www.toutiao.com/")),
        Map())

      if (_resultCaptchaCode != "-1" /*验证码有返回*/ ) {
        val statusCode = response.getStatusLine.getStatusCode

        if (statusCode == HttpStatus.SC_OK /*登陆成功*/ ) { // 登陆流程
          val entity = response.getEntity
          response.getAllHeaders.foreach { h => s"header--> $h" }

          import scala.collection.JavaConversions._
          val cookies = cookieStore.getCookies
          if (cookies.nonEmpty)
            cookies.foreach { o => cookieStore.addCookie(new BasicClientCookie(o.getName, o.getValue)); println(s"add CookieStore --> $o") }
          else
            println("cookies is empty.")

          /**
           * 更新Context CookieStore
           */
          context.setCookieSpecRegistry(registry)
          context.setCookieStore(cookieStore)

          // CookieStr 插入数据库
          val cookieStr = parseCookie(response)
          println("cookieStr --> " + response)
          val metadata = Metadata(account, cookieStr).setType(init)
          QueryHelper.saveMetadata(metadata)

          println(s"login success. account: $account --> 执行登陆成功，Cookie设置成功。")
        } else {
          println(s"login fail, account: $account --> 重试，验证码解析不通过或异常...")
          doLoginService(account, password, getCaptchaStrService)
        }
      } else {
        println(s"login fail, account: $account --> 重试，三方验证码识别错误...")
        doLoginService(account, password, getCaptchaStrService)
      }
    } catch {
      case t: Throwable => println("FAIL --> 执行登陆异常！"); t.printStackTrace()
    }
  }

  /**
   * 点赞
   */
  //  val paramDiggStr = paramDigg(comment_id, dongtai_id, group_id, item_id)
  //  val response = post(url_post_digg, paramDiggStr, postCookie)

  /**
   * 评论
   */
  //	val paramCommentStr = paramComment(group_id, item_id)
  //  val response = post(url_post_post_comment, paramCommentStr, postCookie)

  //	val hEntity = response.getEntity()

  //	println(readContent(hEntity.getContent))

  /**
   * 1. 调用解析验证码接口获取结果字符串
   * 2. 解析结果返回
   */
  def invokeParseCaptcha(captchaStr: String): String = {
    val responseCpatcha = post(url_captcha,
      Map(("convert_to_jpg" -> "0"), ("img_base64" -> captchaStr), ("typeId" -> "34")),
      Map(("Authorization", appCode)))
    val resultCaptchaStr = parseContent(responseCpatcha.getEntity.getContent)
    getResultCaptchaCode(resultCaptchaStr)
  }

  /**
   * 验证码返回结果JSON字符串解析，提取码值
   */
  def getResultCaptchaCode(resultStr: String): String = {
    val jsonObj = JsonMethods.parse(resultStr)
    val showapi_res_code /*0为系统级别成功*/ = (jsonObj \ "showapi_res_code").extract[Integer]
    val ret_code /*0为业务级别成功*/ = (jsonObj \ "showapi_res_body" \ "ret_code").extract[Integer]
    val result = (jsonObj \ "showapi_res_body" \ "Result").extract[String]

    if (showapi_res_code == 0 && ret_code == 0) result else "-1"
  }

  /**
   * 拼接登陆参数请求参数体
   */
  def paramLogin(_params_ : String*): String = {
    assert(_params_.length == 3, "Require _params_ : <account> <password> <captcha>")
    "mobile=" + "sw" + "&code=" + "ss" +
      "&account=" + _params_(0) + "&password=" + _params_(1) + "&captcha=" + _params_(2) +
      "&is_30_days_no_login=false" + "&service=https://www.toutiao.com/"
  }

  /**
   * 测试时方法基于页面
   * http://www.toutiao.com/a6451469307842429198/#p=1
   *
   * 当前用户提交一次评论
   * 浏览器调试发现，每次提交评论时会发送请求： info/ -> post_comment/
   *
   * post_comment 请求参数：
   * status			"___"
   * content		"___"					// 等于status
   * group_id		"___"					// ??? 测试页面值 6451469307842429198
   * item_id		"___"					// ??? 测试页面值 6451472465402003981
   * id					"0" 					// 暂固定值
   * format			"json" 				// 暂固定值
   * aid				"24"					// 暂固定值
   *
   * 测试用参数:
   * content:  String = "这是一条最真实的评论~",
   * group_id: String,
   * item_id:  String,
   * id:       String = "0",
   * format:   String = "json",
   * aid:      String = "24"
   */
  def paramComment(_params_ : String*): String = {
    assert(_params_.length == 2, "Require _params_ : <group_id> <item_id>")
    val content = "这是一条最真实的评论，我也不知道怎么回事~"
    val group_id = _params_(0)
    val item_id = _params_(1)
    val id = "0"
    val format = "json"
    val aid = "24"
    s"status=$content&content=$content&group_id=$group_id&item_id=$item_id&id=$id&format=$format&aid=$aid"
  }

  /**
   * 拼接点赞参数
   *
   */
  def paramDigg(_params_ : String*): String = {
    assert(_params_.length == 4, "Require _params_ : <comment_id> <dongtai_id> <group_id> <item_id>")
    "comment_id=" + _params_(0) + "&dongtai_id=" + _params_(1) +
      "&group_id=" + _params_(2) + "&item_id=" + _params_(3) + "&action=digg"
  }
  
  /**
   * 刷新页面，仅GET请求
   */
  def refreshGET(url: String): String = doGET(url)
  
  /**
   * GET请求，返回响应字符串
   */
  def doGET(url: String): String = parseContent(get(url).getEntity.getContent)

  /**
   * 原生GET请求
   */
  def get(url: String, paramPattern: String = null)(implicit context: HttpContext, client: HttpClient): HttpResponse = {
    val get = new HttpGet(url)
    try
      client.execute(get)
    finally
      println("get finally.")
//      get.releaseConnection()
  }
  
  /**
   * POST，返回响应字符串
   */
  def doPOST(url: String, entity: Map[String, String] = null, headers: Map[String, String] = null): String = parseContent(post(url, entity, headers).getEntity.getContent)

  /**
   * 原生POST请求
   */
  def post(url: String, entity: Map[String, String] = null, headers: Map[String, String] = null)(implicit client: HttpClient): HttpResponse = {
    val post = new HttpPost(url)
    try {
      import scala.collection.JavaConversions._
      if (null != entity) { // 请求参数
        val formData = entity.map(__ => new BasicNameValuePair(__._1, __._2)).toList
        post.setEntity(new UrlEncodedFormEntity(formData, Consts.UTF_8))
      }

      if (null != headers) // 参数
        headers.foreach(__ => post.setHeader(__._1, __._2))
        
      println("POST --> url: " + url + "\nparam: " + entity)
      
      client.execute(post)
    } finally
//      post.releaseConnection()
    println("post fanally.")
  }

  /**
   * 发送带Cookie的POST请求
   * 参数为form提交方式
   */
  def postWithContext(url: String, paramPattern: String, headers: Map[String, String] = null)(implicit context: HttpContext, client: HttpClient): HttpResponse = {
    val post = new HttpPost(url)
    post.setEntity(new StringEntity(paramPattern, ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8)))
    //    post.setHeader("Cookie", postCookie)
    if (null != headers) // 参数
      headers.foreach(__ => post.setHeader(__._1, __._2))

    println("POST --> url: " + url + "\nparam: " + paramPattern)
    client.execute(post, context)
  }

  /**
   * 解析Content流，返回可读字符串
   */
  def parseContent(input: java.io.InputStream): String = {
    import scala.io.Source
    val builder = StringBuilder.newBuilder
    Source.fromInputStream(input).getLines().foreach(__ => builder.append(__).append("\n"))
    builder.toString()
  }

  /**
   * 解析cookieStr 提供Cookie
   */
  def parseCookie(cookieStr: String): CookieStore = {
    val cookieStore = new BasicCookieStore()
    // 获取Cookie
    cookieStr.split(';').foreach { __ =>
      if (__.contains('=')) // 设置Cookie
        cookieStore.addCookie(new BasicClientCookie(__.split('=')(0), __.split('=')(1)))
      else
        cookieStore.addCookie(new BasicClientCookie(__, null))
    }
    cookieStore
  }

  /**
   * 解析出登陆返回Cookie字符串，用于存数据库持久使用
   */
  def parseCookie(response: HttpResponse): String = {
    val builder = new StringBuilder
    response.getAllHeaders.foreach { o => if (o.getName.equalsIgnoreCase(SET_COOKIE)) builder.append(o.getValue) }
    builder.toString()
  }
  
}
