package com.sasaki.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017年10月12日 下午2:24:50
 * @Description 提供标记Clazz
 * 
 */
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Attach2 {
	/**
	 * 拼接查询语句时作为目标列别名
	 * @return
	 */
	public String value() default "";
	/**
	 * 默认被外部处理过程忽略，该字段由手动拼接
	 * @return
	 */
	public boolean ignore() default true;
	
}
