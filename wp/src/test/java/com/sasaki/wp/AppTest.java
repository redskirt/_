package com.sasaki.wp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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
		String capcha = "R0lGODdheAAeAIUAAP////z8/+7u/9PT/8bG/76+/7q6/7e3/7Gx/6ys/5SU/5CQ/4aG/3t7/3d3/3R0/2ho/19f/1tb/1pa/1lZ/1VV/1BQ/0tL/zY2/zEx/ycn/yUl/yQk/xkZ/wQE/wMD/wAA/wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACwAAAAAeAAeAEAI/wABAAgBoKDBgwgTKlzIsKHDhxAbhgBAsaLFiyFCANjIsaPHjyBDigQQAoDJkyhNhlgJoKXLlzBjypxJs6bNmyEAAADBEwCInwCCCgUAAgQAACGSAgABAoDTp09BSAUBoGqIECBAANjKdSsIDyAAiB0LoiwIAGjTql3Ltq3bt3BDgAABoG7dEADy6s0Lom9fAIADCx4MojAIACAAKF7MGACIxwAsAACAAYRlEAAya97MeXOIEABCix5NurTp06hTq17NurXr16FDAJhNu7bt27hz1w7BG4BvCCAACB8uHAQAEACSK1cOornzAB8ASJ9Ovbr169izYw/BnTuI7yHCh/8AQB5AiBAAAIAAAQAEAAAgQACYT7/+fBAgCoAAAKI/AIAABA4kWBDAAhAJASxk2NDhQ4gRJS4MURHARYwZNW7k2NEjxhAARI4kWdIkgAMAVK5k2dLlS5gxZc6kWVNmCAA5de7UGQLAT6BBhQ4FGgLAUaRJlS5l2tTpU6hRpQIIEQLAVaxZr4YA0NXrV7BhxY4lW9as2RBpQwBg29Zt2xAA5M6lW9fuXbx5AYQIAcDvX8CB/YYAUNjwYQAhAIQA0Njx48chJAOgXDkCCBAANG8GcQEECAChRQMAUboBANQgAIAAIQDAa9ixZc+mXZt2CNy5QwAAEALAbxDBAQwnXjz/RAgQIAAsB5AABAgA0aVPrwACBADsAECAmADA+3fwIMSP3wDA/Hn0IUIAYN/e/Xv47kMAoF/f/n38AECAAAAgBMAQAAYSJAjiIAAQIAAwbOiwIQMQEgGAAAEABICMGjWCAGEAAMiQAECQBGDSZAgAKleybOnyJUyYIWaGAAACBICcOnfyBOETANCgQocCBWEUBICkSpcqBeH0KVQAUqdSrWr1KtUQALZy7er1K9iwYseSLWt2bIgQANaybev2Ldy4a0MAqGv3Lt68evfy7esXQIgQAAYTLmz4MOLBIQAwbuy4cQgAkidTrmz5MubMmjWHAOD5M+jPIQCQLm36NOrU/6pXs27t+vXpEABm065t+zbu3LlDhADg+zfw4MKHEy9u/HeIEACWM2/uHEAIAABCAKhu/Tr27Nq3c+/u/Tt4ACHGAwAB4Dz68yDWA2jvvr0EAPLn069v/z7+/Pr32w8BAGAIAAMJFjQ4MERCAAtBNATwECIIiSAAVLRYEURGjRsBdPT4EWRIkSNJhgwBAGVKlStZqgzx8iUAmTNDAABxkwAAnQBA9AQAIAQAoUJBFAVwFCkHEABAAHD6FGpUqVOpVrUKAmtWEAC4dgUAAkSIEADIIgABAkBatWpBAAABAkBcACFAgABwFy+AECFA9AXwF3BgwYMBhwBwGHFixYsZK/8O8RgyCMkhKIcAcBlziBAgQADwDAAECACjSZceDQIEANUAQIAA8Bp27NcgaNeuDQB3bt27effmHQJAcOHDiRcHAAIEAOUAQjR3HgJABxDTAYAAQQFAdu3btYPwDgAECADjyZc3DwAEiAwg2D8AAQB+fPnz5YcAcB9/fv37+fMHARBEiBAAAIQIASChwoQJQAAAAQKABQAUK1qkCCIjABAcAXj8CBKESAAkSwIAgRKAypUsW7p8CTNmyxAAaoYAAQKAzp08e4L4CUADgKFEixoFgRQEgKVMmy4FAWIACABUqSoAAQKA1q1cu3r9Cjbs1hAhAJg9izat2rVs1YJ4CwItgNy5dOuCuIvXAYC9fPv6/Qs4sOAQIQAYPmw4BIDFjBs7fgw5suTJlCtbbhwQADs=";
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
}
