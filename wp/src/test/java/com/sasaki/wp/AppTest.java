package com.sasaki.wp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import sun.misc.BASE64Decoder;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
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
    
    public void test2() throws Exception{
		String capcha = "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";
		try {
			File file = new File("/Users/sasaki/Desktop/t.png");
			if(file.exists()) file.delete();
			FileOutputStream output = new FileOutputStream(file);	
			output.write(getStrToBytes(capcha));
			output.flush();
			output.close();
		} finally {
			
		}
    }
    
    public void test3() {
    	String str = "captcha: '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'})";
    	Pattern pattern = Pattern.compile("captcha: '(.+?)'");
    	Matcher matcher = pattern.matcher(str);
    	if(matcher.find())
    		System.out.println(matcher.group(1));
    }
}
