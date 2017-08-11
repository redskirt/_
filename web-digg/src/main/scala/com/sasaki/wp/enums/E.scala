package com.sasaki.wp.enums

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017��8��11�� ����4:24:32
 * @Description 
 */
object E extends Enumeration {

  val & : String = "&"

  // Cookie Type
  val init = "init"
  val act  = "act"
  
  val SET_COOKIE = "Set-Cookie"
  
  // 验证码接口
  val url_captcha = "https://ali-checkcode2.showapi.com/checkcode"
  val appCode = "APPCODE b8071dda673d41aeb6b17ac75f86d7eb"
  val captcha_regex = "captcha: '(.+?)'"

  val ACCOUNT = "593982054"
  val PASSWORD = "sunshushuai1"

  val DEFAULT_ACCOUNT = "17084117416"
  val DEFAULT_PASSWORD = "lk111222333"

  val www_toutiao_com = "http://www.toutiao.com"
  val sss_toutiao_com = "https://sso.toutiao.com"

  val url_post_post_comment = s"$www_toutiao_com/api/comment/post_comment/"
  val url_post_digg = s"$www_toutiao_com/api/comment/digg/"
  val url_post_account_login = s"$sss_toutiao_com/account_login/"

  val url_get_login = s"$sss_toutiao_com/login/"
  val url_get_usr_info = s"$www_toutiao_com/user/info/"
  val url_get_comment_list = s"$www_toutiao_com/api/comment/list/"

}
