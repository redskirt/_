package com.sasaki.wp.util

import java.io._

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 22, 2018 9:05:23 AM
 * @Description 
 */
protected class NetStreamIOHandler(url: String, pathWithFileName: String) {
  private var inputStream: InputStream = null
  private var byteArrayOutputStream: ByteArrayOutputStream = null
  private var bufferedInputStream: BufferedInputStream = null

  import NetStreamIOHandler._
  
  def download = {
    var inputStream: InputStream = null
    val get = new org.apache.http.client.methods.HttpGet(url)
//    get.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
//    get.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//    get.addHeader("Cookie", "has_js=1; _ga=GA1.2.1240446794.1535084106; _gid=GA1.2.1515941179.1535198952")
//    get.addHeader("Connection", "Keep-Alive")

    try {
      val response = client.execute(get)
//      response.addHeader("Connection", "Keep-Alive")
//      response.addHeader("Cache-Control", "no-cache, must-revalidate")
//      response.addHeader("Content-Type", "image/jpeg")
      
      inputStream = response.getEntity.getContent 
      // 注意，网络传输中避免使用 inputStream.available() 方法获得流的长度
      val streamLength = response.getEntity.getContentLength.toInt
      buildStream2LocalFile(inputStream, streamLength, pathWithFileName)
    } finally {
      if (null != get)
        get.releaseConnection()
      if (null != inputStream)
        inputStream.close()
    }
  }
}

object NetStreamIOHandler {
  
  val client = org.apache.http.impl.client.HttpClients.createDefault()

  def buildStream2LocalFile(
    inputStream:           InputStream,
    streamLength:          Int,
    pathWithFileName:      String) = {

    val bufferedInputStream = new BufferedInputStream(inputStream, streamLength)
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val outputStream: OutputStream = new BufferedOutputStream(new FileOutputStream(pathWithFileName))

    val bytes = new Array[Byte](streamLength)
    try {
      var receivedCount = -1
      while ({
        receivedCount = inputStream.read(bytes, 0, streamLength)
        receivedCount != -1
      }) {
        byteArrayOutputStream.write(bytes, 0, receivedCount)
      }
      
      val bytesOut = byteArrayOutputStream.toByteArray()
      if (bytesOut.size > 0) {
        outputStream.write(bytesOut, 0, bytesOut.length)
        outputStream.flush()
        println(s"Generated file $pathWithFileName.\n>----------------------------------------------------")
      } else 
        println("Ouput stream is empty.")

    } finally {
      if (null != outputStream)
        outputStream.close()
      if (null != byteArrayOutputStream)
        byteArrayOutputStream.close()
      if (null != bufferedInputStream)
        bufferedInputStream.close()
    }
  }
  
  def parseBase64Code2File(base64: String, pathWithName: String) = 
    new FileOutputStream(pathWithName).write(parseBase64Code(base64))
  
  def parseBase64Code(base64: String): Array[Byte] = 
    new sun.misc.BASE64Decoder().decodeBuffer(base64)
  
  def compileBase64Code(file: File): String = compileBase64Code(new FileInputStream(file))
  
  def compileBase64Code(stream: InputStream): String = {
    val length = stream.available()
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val bytes = new Array[Byte](length)
    var receivedCount = -1
      while ({
        receivedCount = stream.read(bytes, 0, length)
        receivedCount != -1
      }) {
        byteArrayOutputStream.write(bytes, 0, receivedCount)
      }
      val bytesOut = byteArrayOutputStream.toByteArray()
      new sun.misc.BASE64Encoder().encode(bytesOut).replace("\n", "")
  }
  
  def apply(url: String, pathWithFileName: String) = new NetStreamIOHandler(url, pathWithFileName)
}

class TryResource[E <: AutoCloseable](protected val resource: E) {
  
}

class TryMultResource[E <: AutoCloseable](protected val resources: List[E]) {
  
}

object TryResource {
  def close[E <: AutoCloseable, T](resource: E*)(fx: Seq[E] => T): T = {
    try {
      fx(resource)
    } finally {
      resource foreach(o => o.close())
    }
  }
  
  def main(args: Array[String]): Unit = {
    //    var inputStream: InputStream = null
    //     val byteArrayOutputStream = new ByteArrayOutputStream()
    //    close(inputStream, byteArrayOutputStream){ o =>
    //      val a:InputStream = o(1).asInstanceOf[InputStream]
    //    }

    //    println {
    //      NetStreamIOHandler.compileBase64Code(new File("/Users/sasaki/vsh/hk/dbImage_ID-15869_No-1.jpeg")).replace("\n", "")
    //    }

    val client = org.apache.http.impl.client.HttpClients.createDefault()
    val get = new org.apache.http.client.methods.HttpGet("https://www.hpcbristol.net/download/image/44618")
    val response = client.execute(get)
    val inputStream = response.getEntity.getContent
    val streamLength = response.getEntity.getContentLength.toInt
    println(response.getEntity.getContentType)
    println(streamLength)
    
//    println {
//      NetStreamIOHandler("https://www.hpcbristol.net/download/image/44618", "/Users/sasaki/Desktop/a.jpg").download
//    }
    
  }
}
