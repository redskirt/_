package com.sasaki.wp.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import sun.misc.BASE64Decoder;

/**
 * @Author 		Sasaki
 * @Timestamp	2017/07/27 22:24:25 
 * @Desc			
 */
public class MockLogin {
	final static String ACCOUNT 			= "lk111222333";
	final static String PASSWORD			= "17084117416";
	final static String URI					= "https://sso.toutiao.com";
	final static String URI_LOGIN			= URI + "/login/";
	final static String URI_LOGIN_SUBMIT	= URI + "account_login/";
	final static String SET_COOKIE			= "Set-Cookie";
	final static String FILE_PATH			= "/Users/sasaki/Desktop/t.png";
	final static String CAPTCHA_REGEX		= "captcha: '(.+?)'";
	
	static CookieStore store = new BasicCookieStore();
	
	public static void main(String[] args) {
		HttpClient client = HttpClientBuilder.create().build();
		try {
			// 本地上下文
			HttpContext localContext = new BasicHttpContext();
			// 绑定本地存储，用于存放Cookie
			localContext.setAttribute(HttpClientContext.COOKIE_STORE, store);

			// GET请求，获取验证码
			HttpGet get = new HttpGet(URI_LOGIN);
			HttpResponse response = client.execute(get);
			int status = response.getStatusLine().getStatusCode();
			println("GET /login response --> " + status);
			if(status == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				println("Response content length --> " + entity.getContentLength());
				File captchaImg = new File(FILE_PATH);
				if(captchaImg.exists()) captchaImg.delete();
				
				InputStream input = entity.getContent();
				// 拼接 /login/ 页面
				BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
				StringBuilder builder = new StringBuilder();
				String line = null;
				while((line = reader.readLine()) != null) {
					println(line);
					builder.append(line);
				}
			
				FileOutputStream output = null;
				try {
					// 正则提取 captcha 字符串
					output = new FileOutputStream(captchaImg);
					String captchaStr = getMatched(builder.toString(), CAPTCHA_REGEX);
					output.write(getStrToBytes(captchaStr));
					output.flush();
				} finally {
					output.close();
					input.close();
				}
			}
			get.releaseConnection();
			
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
					"account=" 		+ ACCOUNT + 
					"&password=" 	+ PASSWORD +
					"&captcha="		+ tCaptcha +
					"&is_30_days_no_login=false&service=https://www.toutiao.com/", "UTF-8");
			post.setEntity(sEntity);
			println("--> request line: " + post.getRequestLine());
			response = client.execute(post);
			
			Header[] headers = response.getAllHeaders();
			Arrays.asList(headers).forEach(__ -> {
				String name = __.getName();
				String value = __.getValue();
				println("--> Response header: " + __.getName() + ":" + __.getValue());				
				
				if(name.equals(SET_COOKIE))  // 获取Cookie
					Arrays.asList(value.split(";")).forEach(___ -> {
	                    String[] cookies = ___.split("=");
	                    println("--> Cookies: " + cookies[0] + ":" + cookies[1]);
	                    store.addCookie(new BasicClientCookie(cookies[0], cookies[1]));
					});
			});
			
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

		}
	}
	
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
