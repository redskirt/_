package com.sasaki.annotation;

import java.lang.reflect.Field;

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年10月12日 下午3:08:46
 * @Description 
 * 
 */
public class TestAnnaotion {
	@Attach2("name")
	String name2;
	@Attach2("id")
	String id2;
	
	public static void main(String[] args) {
		Field[] fields = TestAnnaotion.class.getDeclaredFields();
		for (Field field : fields) {
			Attach2 alias = field.getAnnotation(Attach2.class);
			System.out.println(alias.value());
		}
		
	}
}
