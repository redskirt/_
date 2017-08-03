package com.sasaki.wp.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
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
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import sun.misc.BASE64Decoder;

/**
 * @Author 		Sasaki
 * @Timestamp	2017/07/27 22:24:25 
 * @Desc			
 */
public class MockLogin {
	final static String DEFAULT_ACCOUNT 	= "lk111222333";
	final static String DEFAULT_PASSWORD	= "17084117416";
	final static String URI					= "https://sso.toutiao.com";
	
	final static String URI_LOGIN			= URI + "/login/";
	final static String URI_LOGIN_SUBMIT	= URI + "account_login/";
	final static String SET_COOKIE			= "Set-Cookie";
	final static String FILE_PATH			= "/Users/sasaki/Desktop/t.png";
	final static String CAPTCHA_REGEX		= "captcha: '(.+?)'";
	final static String $toutiao_sso_user	= "toutiao_sso_user";
	
	static CookieStore cookieStore = new BasicCookieStore();
	
	public static void main(String[] args) {
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			// 本地上下文
			HttpContext localContext = new BasicHttpContext();
			// 绑定本地存储，用于存放Cookie
			localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

			String loginContent = contentGET(client, URI_LOGIN);
			String captchaStr = getMatched(loginContent, CAPTCHA_REGEX);
			
			// 生成本地验证码
			generateLocalCaptcha(captchaStr); 
			
			// 阻塞，解盘输入验证码
			println("--> You should be taked captcha image and use keybord to input captcha code please...");
			Scanner scanner = new Scanner(System.in);
			String tCaptcha = scanner.nextLine();
			println("Input captcha " + tCaptcha + ", whil be do POST.");
			scanner.close();
			
			println("--> POST submit...");
			// POST请求，提交登陆
			HttpPost post = new HttpPost(new URI(URI_LOGIN));
			// 请求参数
			StringEntity sEntity = new StringEntity(
					"mobile="		+ "sw" +
					"&code="		+ "ss" +
					"account=" 		+ DEFAULT_ACCOUNT + 
					"&password=" 	+ DEFAULT_PASSWORD +
					"&captcha="		+ tCaptcha +
					"&is_30_days_no_login=false&service=https://www.toutiao.com/", "UTF-8");
			post.setEntity(sEntity);
			
			println("--> request line: " + post.getRequestLine());
			HttpResponse response = client.execute(post);
			
			Header[] headers = response.getAllHeaders();
			Arrays.asList(headers).forEach(__ -> {
				String name = __.getName();
				String value = __.getValue();
				println("--> Response header: " + name + " : " + value);				
				if(name.equalsIgnoreCase(SET_COOKIE))  // 获取Cookie
					Arrays.asList(value.split(";")).forEach(___ -> {// 遍历并设置Cookie
						if(___.contains("=")) {
							String[] cookies = ___.split("=");
							println("--> Cookies: " + cookies[0] + " : " + cookies[1]);
							cookieStore.addCookie(new BasicClientCookie(cookies[0], cookies[1]));
						} else 
							cookieStore.addCookie(new BasicClientCookie(___, null));
					});
			});
			
			// 注册Cookie 到HttpClientContext
			HttpClientContext context = HttpClientContext.create();
			Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider> create()
			.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
			.register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
			.build();
			context.setCookieSpecRegistry(registry);
			context.setCookieStore(cookieStore);

			// 带context继续请求
			HttpResponse response_ = client.execute(new HttpGet("http://www.toutiao.com/a6448591268490477837/"), context);
			InputStream input_ = response_.getEntity().getContent();
			BufferedReader reader_ = new BufferedReader(new InputStreamReader(input_, "UTF-8"));
			String line = null;
			while((line = reader_.readLine()) != null) {
				println(line);
			}
			
//			HttpEntity hEntity = response.getEntity();
//	        println("----------------------------------------");
//	        println(response.getStatusLine());
//	        if(null != hEntity)
//	        	println("Response content length: " + hEntity.getContentLength());
//	        
//	        // 打印结果
//	        BufferedReader reader = new BufferedReader(new InputStreamReader(hEntity.getContent(), "UTF-8"));
//	        String line = null;
//	        while ((line = reader.readLine()) != null) {
//	            println(line);
//	        }
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
