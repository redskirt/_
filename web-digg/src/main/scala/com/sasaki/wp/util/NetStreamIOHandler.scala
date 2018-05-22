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

    try {
      val response = client.execute(get)
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
  
  def apply(url: String, pathWithFileName: String) = new NetStreamIOHandler(url, pathWithFileName)
}