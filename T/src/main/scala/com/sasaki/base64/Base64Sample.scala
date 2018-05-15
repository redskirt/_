package com.sasaki.base64

import java.io.FileInputStream

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 14, 2018 6:56:36 PM
 * @Description 
 */
object Base64Sample {
  
  def main(args: Array[String]): Unit = {
    val fileName = "/Users/sasaki/Desktop/c.jpg"
    val inputStream = new FileInputStream(fileName)
    val bytes = new Array[Byte](inputStream.available())
    inputStream.read(bytes)
    inputStream.close()
    
    val encoder = new sun.misc.BASE64Encoder()
    println(encoder.encode(bytes))
    
    
  }
}