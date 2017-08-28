package com.sasaki.chain;

import java.util.ArrayList;

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年4月6日 下午12:04:02
 * @Description 
 * 
 */
public class UtilArrayList<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * List 前置add方法
	 * @param e
	 * @return
	 */
	public UtilArrayList<E> _add(E e){
		add((E) e);
		return this;
	}
	
	
}
