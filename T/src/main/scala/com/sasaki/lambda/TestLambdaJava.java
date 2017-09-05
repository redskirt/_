package com.sasaki.lambda;
/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年9月5日 下午3:07:41
 * @Description 
 * 
 */
public class TestLambdaJava {
	interface Anyt {
		public int age();
	}
	
	public static int getAge(Anyt any) {
		return any.age();
	}
	
	public static void main(String[] args) {
		System.out.println(getAge(new Anyt() {
			@Override
			public int age() {
				return 20;
			}
		}));
		System.out.println(getAge(() -> 20));
	}
}

