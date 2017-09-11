package com.sasaki.lambda;

import java.util.Map;
import java.util.stream.Collectors;

import com.sasaki.chain.UtilArrayList;

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年9月11日 下午4:42:13
 * @Description 
 * 
 */
public class Account {
	private Integer id;
	private String name;
	private int age;

	public static void main(String[] args) {
		UtilArrayList<Account> list = new UtilArrayList<>();
		Account account1 = new Account(1, "n1", 10);
		Account account2 = new Account(2, "n2", 20);
		Account account3 = new Account(3, "n3", 40);
		Account account4 = new Account(4, "n4", 30);
		list._add(account1)._add(account2)._add(account3)._add(account4);
//		list.forEach(System.out::println);
		// Lambda
		Map<Integer, Account> map = list.stream()
			.map(__ -> __.setName(__.getName().toUpperCase()))// 名字改大写
			.filter(__ -> __.getAge() > 20) // 过滤>20
			.collect(Collectors.toMap(__ -> __.getId(), __ -> __)); // List转Map
		map.entrySet().forEach(System.out::println);
		
	}
	
	public Account(Integer id, String name, int age) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Account setName(String name) {
		this.name = name;
		return this;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}



}
