package com.sasaki.wp.sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * @Author 		Sasaki
 * @Timestamp	2017/07/27 22:24:25 
 * @Desc			
 */
public class MockLogin {
	final static String USERNAME 	= "lk111222333";
	final static String PASSWORD	= "17084117416";
	final static String URI			= "https://www.toutiao.com";
	final static String SET_COOKIE	= "Set-Cookie";
	
	static CookieStore store = new BasicCookieStore();
	
	public static void main(String[] args) {
		HttpClient client = HttpClientBuilder.create().build();
		try {
			// 本地上下文
			HttpContext localContext = new BasicHttpContext();
			// 绑定本地存储，用于存放Cookie
			localContext.setAttribute(HttpClientContext.COOKIE_STORE, store);
			
			HttpPost post = new HttpPost(new URI(URI + "/"));
			println("request --> " + post.getRequestLine());
			
			// 请求参数
			StringEntity sEntity = new StringEntity("username=admin&password=admin", "UTF-8");
			post.setEntity(sEntity);
			
			// 执行
			HttpResponse response = client.execute(post);
			
			Header[] headers = response.getAllHeaders();
			Arrays.asList(headers).forEach(__ -> {
				String name = __.getName();
				String value = __.getValue();
				System.out.println("header : " + __.getName() + ":" + __.getValue());				
				
				if(name.equals(SET_COOKIE))  // 获取Cookie
					Arrays.asList(value.split(";")).forEach(___ -> {
	                    String[] cookies = ___.split("=");
	                    println("=============== : " + cookies[0] + ":" + cookies[1]);
	                    store.addCookie(new BasicClientCookie(cookies[0], cookies[1]));
					});
			});
			
			HttpEntity hEntity = response.getEntity();
	        println("----------------------------------------");
	        println(response.getStatusLine());
	        if(null != hEntity)
	        	println("Response content length: " + hEntity.getContentLength());
	        
	        // 打印结果
	        BufferedReader reader = new BufferedReader(new InputStreamReader(hEntity.getContent(), "UTF-8"));
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            println(line);
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}
	
	static void println(Object message) { System.out.println(message); }

}
