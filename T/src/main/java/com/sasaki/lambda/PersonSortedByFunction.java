package com.sasaki.lambda;

import java.util.Collections;
import java.util.Comparator;

import com.sasaki.chain.UtilArrayList;

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年5月27日 上午11:57:40
 * @Description
 * 
 */
public class PersonSortedByFunction {

	public static void main(String[] args) {
		UtilArrayList<Person> list = new UtilArrayList<>();
		Person p1 = new Person("sasaki", 12);
		Person p2 = new Person("Tom", 24);
		Person p3 = new Person("Jerry", 32);
		Person p4 = new Person("Nicholas", 8);

		list._add(p1)._add(p2)._add(p4)._add(p3);

//		Collections.sort(list, new Comparator<Person>() {
//			@Override
//			public int compare(Person p, Person p_) {
//				return p.getAge().compareTo(p_.getAge());
//			}
//		});
//		
//		Collections.sort(list, (p, p_) -> p.getAge().compareTo(p_.getAge()));
		
//		Collections.sort(list, Comparator.comparing((Person p) -> p.getAge()));
		
		Collections.sort(list, Comparator.comparing(Person::getAge).reversed());
		
		list.forEach(System.out::println);
		
	}
}

class Person {
	private String name;
	private Integer age;

	public Person(String name, Integer age) {
		super();
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + "]";
	}

}
