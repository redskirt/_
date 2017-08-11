package com.sasaki.wp.sample

import java.io.InputStream

import org.apache.http.Consts
import org.apache.http.HttpResponse
import org.apache.http.client.CookieStore
import org.apache.http.client.HttpClient
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.cookie.CookieSpecProvider
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.http.impl.cookie.DefaultCookieSpecProvider
import org.apache.http.protocol.HttpContext

import com.sasaki.wp.util.Util
import org.apache.http.client.config.RequestConfig
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.HttpEntity

import org.json4s._
import org.json4s.jackson.JsonMethods._
    
/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-08-08 上午10:54:09
 * @Description 
 */
class WebDigg {
  
}

object WebDigg extends App {
  import com.sasaki.wp.sample.E._
  
  implicit val formats = DefaultFormats
  implicit val context = HttpClientContext.create() 
  implicit val client = HttpClients.createDefault()
  implicit val registry: Registry[CookieSpecProvider] = RegistryBuilder.create[CookieSpecProvider]()
    .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider())
    .build
  
  val cookieStr = "UM_distinctid=15db62375bf520-07e30c1eda903a-143a6d54-13c680-15db62375c0424; uuid=\"w:aa24cc220e7a418bb6e4cffa8be3c448\"; login_flag=812f9a7d1d30496c2f308e906425d8cd; sessionid=3130dab04efadc6c8c35e871de3291bd; uid_tt=bd07b45d64ce252fe3fd023283ded123; sid_tt=3130dab04efadc6c8c35e871de3291bd; sid_guard=\"3130dab04efadc6c8c35e871de3291bd|1502011604|2591999|Tue\054 05-Sep-2017 09:26:43 GMT\"; sso_login_status=1; csrftoken=5c0e8529e5ad23b900963421010af5cb; WEATHER_CITY=%E5%8C%97%E4%BA%AC; tt_webid=6451038131775948302; CNZZDATA1259612802=1700012720-1501999169-%7C1502030762; __tasessionId=o5qe4kut81502032004628"
  val postCookie = """csrftoken=099c9203c938060b4f1ea3dce16ab1a1; tt_webid=6447316118323512846; WEATHER_CITY=%E5%8C%97%E4%BA%AC; UM_distinctid=15d827c2ccf51-0963e2cc5326bc8-41554330-1fa400-15d827c2cd038c; CNZZDATA1259612802=407746919-1501128910-https%253A%252F%252Fwww.bing.com%252F%7C1502152511; uuid="w:82ca84223a28448cb7dfda2dfa5eab1c"; sso_login_status=1; login_flag=0c02740c9e36917cafaabd4768f9ec29; sessionid=23db5f93ebc0295196623c8cbf22f3d1; uid_tt=18383eb38585d5a3988fa25d7eb5fe9a; sid_tt=23db5f93ebc0295196623c8cbf22f3d1; sid_guard="23db5f93ebc0295196623c8cbf22f3d1|1502084673|2591999|Wed\054 06-Sep-2017 05:44:32 GMT"; __tasessionId=e5qz7x4j21502155726741"""
  
  context.setCookieSpecRegistry(registry)
	context.setCookieStore(parseCookie(cookieStr))
	  
  val comment_id = "1575095098176542"
  val dongtai_id = "1575095098176542"
  val group_id   = "6451469307842429198"
  val item_id    = "6451472465402003981"

  /**
   * 登陆前，获取验证码
   */
  //  val loginContent: InputStream = get(url_get_login).getEntity.getContent
  //  val captchaStr = Util.getMatched(parseContent(loginContent), captcha_regex)
  //  println("captchaStr --> " + captchaStr)

  /**
   * 调用解析验证码接口获取解析字符串
   *
   */
  //  val responseCpatcha = post(url_captcha,
  //    Map(("convert_to_jpg" -> "0"), ("img_base64" -> captchaStr), ("typeId" -> "34")),
  //    Map(("Authorization", appCode)))
  //  val parsedCaptcharCode = parseContent(responseCpatcha.getEntity.getContent)  
  //  println("parsedCaptcharCode --> " + parsedCaptcharCode)

  val str = """
    {"showapi_res_code":0,"showapi_res_error":"","showapi_res_body":{"Result":"haue","ret_code":0,"Id":"95f4d921-78c8-41bf-bc78-48a33ca9be56"}}
  """
  println(getParsedCaptchaCode(str))

  
  /**
   * 登陆，三方验证码解析
   */
  def login(account: String, password: String, captchaStr: String): (Boolean, HttpResponse) = {

    null
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
   * 
   */
  def getParsedCaptchaCode(resultStr: String): String = {
    val jsonObj = parse(resultStr)
    val showapi_res_code/*0为系统级成功*/ = (jsonObj \ "showapi_res_code").extract[Integer]
    val ret_code/*0为业务成功*/ = (jsonObj \ "showapi_res_body" \ "ret_code").extract[Integer]
    val result  = (jsonObj \ "showapi_res_body" \ "Result").extract[String]
    
    if(showapi_res_code == 0 && ret_code == 0) result else "-1"
  }
  
  /**
   * 
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
	  val content    = "这是一条最真实的评论，我也不知道怎么回事~"
    val group_id   = _params_(0)   
    val item_id    = _params_(1)    
    val id         = "0"
    val format     = "json"
    val aid        = "24"
    s"status=$content&content=$content&group_id=$group_id&item_id=$item_id&id=$id&format=$format&aid=$aid"
  }

  /**
   * 拼接点赞参数
	 *
   */
  def paramDigg(_params_ : String*): String = {
    assert(_params_.length == 4, "Require _params_ : <comment_id> <dongtai_id> <group_id> <item_id>")
    "comment_id=" + _params_(0) +  "&dongtai_id=" + _params_(1) + 
    "&group_id="+ _params_(2) + "&item_id="+ _params_(3) + "&action=digg"
  }
  
  def get(url: String, paramPattern: String = null)(implicit context: HttpContext, client: HttpClient): HttpResponse = {
    val get = new HttpGet(url)
    client.execute(get)
  }

  /**
   * 带参数体POST方法
   */
  def post(url: String, entity: Map[String, String] = null, headers: Map[String, String] = null)(implicit client: HttpClient): HttpResponse = {
    val post = new HttpPost(url)
    
    import scala.collection.JavaConversions._
    if(null != entity) { // 请求参数
    	val formData = entity.map(__ => new BasicNameValuePair(__._1, __._2)).toList
    	post.setEntity(new UrlEncodedFormEntity(formData, Consts.UTF_8))
    }
    
    //    post.setHeader("Cookie", postCookie)
    if (null != headers) // 参数
      headers.foreach(__ => post.setHeader(__._1, __._2))
   
    println("POST --> url: " + url + "\nparam: " + entity)
    client.execute(post)
  }
  
  /**
   * 发送带Cookie的POST请求
   * 参数为form提交方式
   */
  def postWithContext(url: String, paramPattern: String, headers: Map[String, String] = null)(implicit context: HttpContext, client: HttpClient): HttpResponse = {
    val post = new HttpPost(url)
    post.setEntity(new StringEntity(paramPattern, ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8)))
//    post.setHeader("Cookie", postCookie)
    if(null != headers) // 参数
      headers.foreach(__ => post.setHeader(__._1, __._2))
      
    println("POST --> url: " + url + "\nparam: " + paramPattern )
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
		  if(__.contains('=')) // 设置Cookie
				cookieStore.addCookie(new BasicClientCookie(__.split('=')(0), __.split('=')(1)))
			else 
				cookieStore.addCookie(new BasicClientCookie(__, null))  
		}
		cookieStore
	}
}
  

object E extends Enumeration {
  
  val & : String = "&"

  // 验证码接口
  val url_captcha    = "https://ali-checkcode2.showapi.com/checkcode"
  val appCode        = "APPCODE b8071dda673d41aeb6b17ac75f86d7eb"
  val captcha_regex  = "captcha: '(.+?)'"
  
  val ACCOUNT        = "593982054"
  val PASSWORD       = "sunshushuai1"
  
  val DEFAULT_ACCOUNT 	= "17084117416"
  val DEFAULT_PASSWORD	= "lk111222333"
  

  val www_toutiao_com         = "http://www.toutiao.com"
  val sss_toutiao_com         = "https://sso.toutiao.com"
  
  val url_post_post_comment   = s"$www_toutiao_com/api/comment/post_comment/"
  val url_post_digg           = s"$www_toutiao_com/api/comment/digg/"
  val url_post_account_login  = s"$sss_toutiao_com/account_login/"
  
  val url_get_login           = s"$sss_toutiao_com/login/"
  val url_get_usr_info        = s"$www_toutiao_com/user/info/"
  val url_get_comment_list    = s"$www_toutiao_com/api/comment/list/"
  
  val context = HttpClientContext.create()
  
}