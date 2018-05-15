package com.sasaki.wp.sample

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import com.sasaki.wp.persistence.QueryHelper
import com.sasaki.wp.persistence.QueryHelper

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 13, 2018 10:30:32 PM
 * @Description
 */

object VirtualShanghaiWebDigg extends QueryHelper {

  val basePath = "http://www.virtualshanghai.net/"
  //  val url = "http://www.virtualshanghai.net/Photos/Images?ID=3"
  val imageName = (id: Long) => s"dbImage_ID-${id}_No-1.jpeg"
  val url = (id: Long) => s"http://www.virtualshanghai.net/Asset/Preview/${imageName(id)}"
  //  val url = "http://img6.3lian.com/c23/desk4/05/77/d/01.jpg"
  //  val url = "http://www.virtualshanghai.net/Asset/Preview/dbImage_ID-5_No-1.jpeg"

  /**
   * 解析Response，即Content
   */
  def parseResponse(response: HttpResponse): String = parseContent(response.getEntity.getContent)

  /**
   * 解析Content流，返回可读字符串
   */
  def parseContent(input: java.io.InputStream): String = {
    import scala.io.Source
    val builder = StringBuilder.newBuilder
    Source.fromInputStream(input, "UTF-8").getLines().foreach(__ => builder.append(__).append("\n"))
    builder.toString()
  }

  def erase(that: String, specify: String): String = that.replace(specify, "")

  def main(args: Array[String]): Unit = {

    val id = 3

    val client = HttpClients.createDefault()
    val get = new HttpGet(url(id))
    var inputStream: InputStream = null
    var bufferedOutputStream: OutputStream = null
    var bufferedInputStream: BufferedInputStream = null
    var outStream: ByteArrayOutputStream = new ByteArrayOutputStream()
    try {
      val response: HttpResponse = client.execute(get)
      //      println(parseResponse(response))
      inputStream = response.getEntity.getContent
      bufferedInputStream = new BufferedInputStream(inputStream);

      val length = bufferedInputStream.available()
      var bytes = new Array[Byte](length)

      //      val buffer = new StringBuffer()
      //      val encoder = new sun.misc.BASE64Encoder()

      var count = -1
      while ({
        count = inputStream.read(bytes, 0, length)
        count != -1
      }) {
        outStream.write(bytes, 0, count)
      }
      val bytes_ = outStream.toByteArray()
      
      //      val decoder = new sun.misc.BASE64Decoder()
      //      buffer.append(encoder.encode(bytes_))

      //      val base64Image = erase(buffer.toString(), "\n")
      //      println(base64Image)

      import com.sasaki.wp.persistence.poso._
      // 仅创建带page_id记录，与照片名page_id一致
      val source = Source(id, "", "")
      source.imageName = imageName(id)
      saveSource(source)

      //生成jpeg图片
      val fileName = s"/Users/sasaki/Desktop/vsh/sh/${imageName(id)}"
      val file = new File(fileName)
      if (file.exists())
        file.delete()
      bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileName));
      bufferedOutputStream.write(bytes_, 0, bytes_.length)
      bufferedOutputStream.flush()

    } finally {
      println("get finally.")
      get.releaseConnection()
      if (null != inputStream)
        inputStream.close()
      if (null != bufferedOutputStream)
        bufferedOutputStream.close()
    }
  }
}

