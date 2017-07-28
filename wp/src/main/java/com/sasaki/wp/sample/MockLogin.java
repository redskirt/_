package com.sasaki.wp.sample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import sun.misc.BASE64Decoder;

/**
 * @Author 		Sasaki
 * @Timestamp	2017/07/27 22:24:25 
 * @Desc			
 */
public class MockLogin {
	final static String USERNAME 	= "lk111222333";
	final static String PASSWORD	= "17084117416";
	final static String URI			= "https://sso.toutiao.com";
	final static String URI_LOGIN	= "https://sso.toutiao.com/login";
	final static String URI_LOGIN_SUBMIT	= "https://sso.toutiao.com/account_login";
	final static String SET_COOKIE	= "Set-Cookie";
	final static String FILE_PATH	= "/Users/sasaki/Desktop/t.png";
	
	static CookieStore store = new BasicCookieStore();
	
	public static void main(String[] args) {
		HttpClient client = HttpClientBuilder.create().build();
		try {
			// 本地上下文
			HttpContext localContext = new BasicHttpContext();
			// 绑定本地存储，用于存放Cookie
			localContext.setAttribute(HttpClientContext.COOKIE_STORE, store);

			// GET请求，获取验证码
			HttpGet get = new HttpGet(URI + "/refresh_captcha/");
			HttpResponse response = client.execute(get);
			int status = response.getStatusLine().getStatusCode();
			if(status == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				File file = new File(FILE_PATH);
				if(file.exists()) file.delete();
				InputStream input = entity.getContent();
				
				String capcha = "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";
				BASE64Decoder decoder = new BASE64Decoder();
				byte[] bytes_ = decoder.decodeBuffer(capcha);
				println(bytes_.length);
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(file);
					byte[] bytes = new byte[2048];
					while(input.read(bytes_) != -1) 
						output.write(bytes_);
					output.flush();
				} finally {
					output.close();
					input.close();
				}
			}
			
//			get.releaseConnection();
			
			
			// POST请求，提交登陆
//			HttpPost post = new HttpPost(new URI(URI_LOGIN));
//			println("request --> " + post.getRequestLine());
//			
//			// 请求参数
//			StringEntity sEntity = new StringEntity(
//					"account=" + USERNAME + 
//					"&password=" + PASSWORD +
//					"&is_30_days_no_login=false&captcha=ssee&service=https://www.toutiao.com/", "UTF-8");
//			post.setEntity(sEntity);
			
			// 执行
//			HttpResponse response_ = client.execute(post);
//			
//			Header[] headers = response_.getAllHeaders();
//			Arrays.asList(headers).forEach(__ -> {
//				String name = __.getName();
//				String value = __.getValue();
//				System.out.println("header : " + __.getName() + ":" + __.getValue());				
//				
//				if(name.equals(SET_COOKIE))  // 获取Cookie
//					Arrays.asList(value.split(";")).forEach(___ -> {
//	                    String[] cookies = ___.split("=");
//	                    println("=============== : " + cookies[0] + ":" + cookies[1]);
//	                    store.addCookie(new BasicClientCookie(cookies[0], cookies[1]));
//					});
//			});
			
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
	
	static void println(Object message) { System.out.println(message); }

}
