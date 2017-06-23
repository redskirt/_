package com.sasaki.fp

import com.sun.xml.internal.ws.server.sei.EndpointArgumentsBuilder.StringBuilder

object FpSample {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

	// 对x是“引用透明”特性的说明
  val x = "Hello, World"                          //> x  : String = Hello, World

	val r1 = x.reverse                        //> r1  : String = dlroW ,olleH
	val r2 = x.reverse                        //> r2  : String = dlroW ,olleH
	
	val r1_ = "Hello, World".reverse          //> r1_  : String = dlroW ,olleH
	val r2_ = "Hello, World".reverse          //> r2_  : String = dlroW ,olleH

	// 相比x，y则不是“引用透明”的，使用与y等价的表达式替换计算得出了不一致的结果
	val y = new StringBuilder("Hello")        //> y  : StringBuilder = Hello
	
	val y_ = y.append(", World")              //> y_  : StringBuilder = Hello, World
	
	val r3 = y_                               //> r3  : StringBuilder = Hello, World
	val r4 = y_                               //> r4  : StringBuilder = Hello, World
	
	// 把y_替换成其引用的表达式，则不再相等
	val r3_ = y.append(", World")             //> r3_  : StringBuilder = Hello, World, World
	val r4_ = y.append(", World")             //> r4_  : StringBuilder = Hello, World, World, World
	
}