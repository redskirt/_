package com.sasaki.o;

import java.util.List;
import java.util.ArrayList;


/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年12月11日 上午11:48:31
 * @Description 
 * 
 */
public class JavaExpression {

	public static void main(String[] args) {
		Integer flag = null;
		// 忽略永真条件，对flag赋值
		if(true) 
			flag = 0;
		else 
			flag = 1;
		
		// 忽略Null情况，截取首字符
		String s = "abc";
		String sSub = null;
			if(!s.isEmpty())
				sSub = s.substring(1);
			else
				sSub = "";
			
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
			
	}

}
