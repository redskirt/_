package com.sasaki.wp.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.CharEncoding;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;

import sun.misc.BASE64Decoder;

/**
 * @Author 		Sasaki
 * @Timestamp	2017/07/27 22:24:25 
 * @Desc			
 */
public class MockLogin {
	final static String DEFAULT_ACCOUNT 	= "17084117416";
	final static String DEFAULT_PASSWORD	= "lk111222333";
	final static String URI					= "https://sso.toutiao.com";
	final static String URI_WWW				= "http://www.toutiao.com";
	
	final static String URI_LOGIN			= URI + "/login/";
	final static String URI_LOGIN_SUBMIT	= URI + "account_login/";
	final static String URI_LOGIN_DIGG		= URI_WWW + "/api/comment/digg/";
	
	final static String SET_COOKIE			= "Set-Cookie";
	final static String FILE_PATH			= "/Users/sasaki/Desktop/t.png";
	final static String CAPTCHA_REGEX		= "captcha: '(.+?)'";
	final static String $toutiao_sso_user	= "toutiao_sso_user";
	static String cookieStr = "toutiao_sso_user=3460efc068605b814faa04dc84184be3; Domain=sso.toutiao.com; expires=Sun, 10-Sep-2017 06:15:16 GMT; httponly; Max-Age=3024000; Path=/"
			+ "sso_login_status=0; Domain=toutiao.com; expires=Sun, 10-Sep-2017 06:15:16 GMT; httponly; Max-Age=3024000; Path=/";
	
	public static void main(String[] args) {
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			// 本地上下文
//			HttpContext localContext = new BasicHttpContext();
			// 绑定本地存储，用于存放Cookie
//			localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

//			String loginContent = contentGET(client, URI_LOGIN);
//			String captchaStr = getMatched(loginContent, CAPTCHA_REGEX);
			
			// 生成本地验证码
//			generateLocalCaptcha(captchaStr); 
			
			// 阻塞，解盘输入验证码
//			println("--> You should be taked captcha image and use keybord to input captcha code please...");
//			Scanner scanner = new Scanner(System.in);
//			String tCaptcha = scanner.nextLine();
//			println("Input captcha " + tCaptcha + ", whil be do POST.");
//			scanner.close();
			
//			println("--> POST submit...");
//			HttpClientContext context = getContextWithPost(client, tCaptcha, URI_LOGIN, DEFAULT_ACCOUNT);
			
			HttpClientContext context = HttpClientContext.create();
			Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider> create()
			.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory()/*DefaultCookieSpecProvider*/)
			.register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
			.build();
			context.setCookieSpecRegistry(registry);
			context.setCookieStore(parseCookie(cookieStr));
			// 带context继续请求
//			HttpResponse response_ = client.execute(new HttpGet("http://www.toutiao.com/i6450092422402671117/?wxshare_count=2"), context);
//			InputStream input_ = response_.getEntity().getContent();
//			BufferedReader reader_ = new BufferedReader(new InputStreamReader(input_, CharEncoding.UTF_8));
//			String line = null;
//			while((line = reader_.readLine()) != null) {
//				println(line);
//			}
		
			HttpGet get1 = new HttpGet("http://www.toutiao.com/user/info/");
			client.execute(get1, context);
			
			URI uri2 = new URIBuilder()
			.setScheme("http")
			.setHost("ei.cnzz.com")
			.setPath("/stat.htm")
			.setParameter("id", "1259612802")
			.setParameter("r", "")
			.setParameter("lg", "zh-cn")
			.setParameter("ntime", "1502009158")
			.setParameter("cnzz_eid", "362386540-1501164244-https://www.google.co.jp/")
			.setParameter("showp", "1440x900")
			.setParameter("ei", "detail_article|click_good_comment||1|")
			.setParameter("t", "难怪要抛弃ZUK，联想新机将骁龙430卖到快2000")
			.setParameter("umuuid", "15d84615cf81d0-0d986178526d538-7f682331-13c680-15d84615cf91c2")
			.setParameter("h", "1")
			.setParameter("md", "335162160")
			.build();
			HttpGet get2 = new HttpGet(uri2);
			println(get2.getURI());
			client.execute(get2, context);
			
			URI uri3 = new URIBuilder()
			.setScheme("http")
			.setHost("www.toutiao.com")
			.setPath("/api/article/user_log/")
			.setParameter("c", "detail_article")
			.setParameter("ev", "click_good_comment")
			.setParameter("sid", "tq2fj9cga1502022343455")
			.setParameter("type", "event")
			.setParameter("t", "1502023878666")
			.build();
			HttpGet get3 = new HttpGet(uri3);
			println(get3.getURI());
			client.execute(get3, context);
			
			String postDigg = "&comment_id=" + "1574770775356429" +
							  "&dongtai_id=" + "1574770775356429" +
							  "&group_id=" + "6450085980351578382" +
							  "&item_id=" + "6450092422402671117" + 
							  "&action=digg";
			StringEntity postDiggEntity = new StringEntity(postDigg, CharEncoding.UTF_8);
			HttpPost post = new HttpPost("http://www.toutiao.com/api/comment/digg/");
			post.setEntity(postDiggEntity);
			println(post.getURI());
			HttpResponse response = client.execute(post, context);
			HttpEntity hEntity = response.getEntity();
	        println("----------------------------------------");
	        println(response.getStatusLine());
	        if(null != hEntity)
	        	println("Response content length: " + hEntity.getContentLength());
//	        // 打印结果
	        BufferedReader reader = new BufferedReader(new InputStreamReader(hEntity.getContent(), CharEncoding.UTF_8));
	        String line_ = null;
	        while ((line_ = reader.readLine()) != null) {
	            println(line_);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static HttpClientContext getContextWithPost(CloseableHttpClient client, String tCaptcha, String uri, String account) throws Exception {
		// POST请求，提交登陆
		HttpPost post = new HttpPost(new URI(uri));
		
		String sEntityStr = 
				"mobile="		+ "sw" +
				"&code="		+ "ss" +
				"account=" 		+ account + 
				"&password=" 	+ DEFAULT_PASSWORD +
				"&captcha="		+ tCaptcha +
				"&is_30_days_no_login=false&service=https://www.toutiao.com/";
				
		// 请求参数
		StringEntity sEntity = new StringEntity(sEntityStr, CharEncoding.UTF_8);
		post.setEntity(sEntity);
		
		println("--> request line: " + post.getRequestLine());
		HttpResponse response = client.execute(post);
		
		// 从Response获取、注册Cookie
		CookieStore cookieStore = parseCookie(response);
		
		// 注册Cookie 到HttpClientContext
		HttpClientContext context = HttpClientContext.create();
		Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider> create()
		.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory()/*DefaultCookieSpecProvider*/)
		.register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
		.build();
		
		context.setCookieSpecRegistry(registry);
		context.setCookieStore(cookieStore);
		return context;
	}

	private static CookieStore parseCookie(HttpResponse response) {
		CookieStore cookieStore = new BasicCookieStore();
		Header[] headers = response.getAllHeaders();
		Arrays.asList(headers).forEach(__ -> {
			String name = __.getName();
			String value = __.getValue();
			println("--> Response header: " + name + " : " + value);
			if(name.equalsIgnoreCase(SET_COOKIE)) 
				parseCookie(value);
		});
		return cookieStore;
	}
	
	private static CookieStore parseCookie(String cookieStr) {
		CookieStore cookieStore = new BasicCookieStore();
		// 获取Cookie
		Arrays.asList(cookieStr.split(";")).forEach(___ -> {// 遍历并设置Cookie
			if(___.contains("=")) // 设置Cookie
				cookieStore.addCookie(new BasicClientCookie(___.split("=")[0], ___.split("=")[1]));
			else 
				cookieStore.addCookie(new BasicClientCookie(___, null));
		});
		return cookieStore;
	}
	
	private static void generateLocalCaptcha(String captchaStr) throws Exception {
		File captchaImg = new File(FILE_PATH);
		if(captchaImg.exists()) 
			captchaImg.delete();
		
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(captchaImg);
			output.write(getStrToBytes(captchaStr));
			output.flush();
		} finally {
			output.close();
		}
	}
	
	/**
	 * 
	 * @param client
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private static String contentGET(CloseableHttpClient client, String url) throws Exception {
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		StringBuilder builder = new StringBuilder("");
		int status = response.getStatusLine().getStatusCode();
		println("GET " + url + " response --> " + status);
		
		if(status == HttpStatus.SC_OK) {
			HttpEntity entity = response.getEntity();
			println("Response content length --> " + entity.getContentLength());
			
			InputStream input = entity.getContent();
			// 拼接页面字符串
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String line = null;
			while((line = reader.readLine()) != null) 
				builder.append(line);
		}
		get.releaseConnection();
		
		return builder.toString();
	}
	
	/**
	 * 
	 * @param imgStr
	 * @return
	 */
	@SuppressWarnings("restriction")
    public static byte[] getStrToBytes(String imgStr) {   
        if (imgStr == null) // 图像数据为空  
            return null;  
		BASE64Decoder decoder = new BASE64Decoder();  
        try {  
            // Base64解码  
            byte[] bytes = decoder.decodeBuffer(imgStr);  
            for (int i = 0; i < bytes.length; ++i) {  
                if (bytes[i] < 0) {// 调整异常数据  
                    bytes[i] += 256;  
                }  
            }  
            // 生成jpeg图片  
            return bytes;  
        } catch (Exception e) {  
            return null;  
        }  
    }
    
    public static String getMatched(String str, String regex) {
    	Pattern pattern = Pattern.compile(regex);
    	Matcher matcher = pattern.matcher(str);
    	if(matcher.find()) 
    		return matcher.group(1);
    	return null;
    }
    
	static void println(Object message) { System.out.println(message); }

}
