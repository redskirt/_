package com.sasaki.chain;

import java.util.HashMap;

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年4月6日 上午11:16:41
 * @Description
 * 
 */
public class UtilHashMap<K, V> extends HashMap<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Map 前置put方法
	 * 
	 * @param _key_
	 * @param _value_
	 * @return
	 */
	public UtilHashMap<K, V> _put(K _key_, V _value_) {
		put(_key_, _value_);
		return this;
	}
	
	public static void main(String[] args) {
		UtilHashMap<String, Object> map = new UtilHashMap<>();
		map.put("id", 1);
		map.put("name", "Sasaki");
		map.put("age", 23);
		map.put("gender", "man");
		
		UtilHashMap<String, Object> map_ = new UtilHashMap<>();
		map_._put("id", 1)._put("name", "Sasaki")._put("age", 23)._put("gender", "man");
	}
}
