package com.sasaki.wp.sample

import java.io._

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import com.sasaki.wp.persistence.QueryHelper
import java.util.concurrent.Executors

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 13, 2018 10:30:32 PM
 * @Description
 */

object VirtualShanghaiWebDigg {

  val threadPool = Executors.newFixedThreadPool(1)

  def main(args: Array[String]): Unit = {
    try {
      // DOTO 13200 ~ 30000
//      for (i <- 23326 to 30000) 
//        threadPool.execute(new ImageFetchProcess(i))
      threadPool.execute(new ContentFetchProcess())
    } finally 
      threadPool.shutdown() 
  }
}

class ContentFetchProcess() extends Runnable with QueryHelper {
  import ProcessUtil._
  
  override def run() {
    val get = new HttpGet(urlContent)
    val response: HttpResponse = client.execute(get)
    
    try {
      val pageHtml = parseResponse(response)
      println(pageHtml)
    } catch {
      case t: Throwable => t.printStackTrace() // TODO: handle error
    } finally {
      if(null != get)
        get.releaseConnection()
    }
  }
  
}

class ImageFetchProcess(id: Int) extends Runnable with QueryHelper {

  import ProcessUtil._

  var inputStream: InputStream = null
  var bufferedOutputStream: OutputStream = null
  var bufferedInputStream: BufferedInputStream = null
  var outputStream: ByteArrayOutputStream = null

  override def run() {
    val get = new HttpGet(urlImage(id))
    val response: HttpResponse = client.execute(get)
    try {
      //      println(parseResponse(response))
      inputStream = response.getEntity.getContent
      outputStream = new ByteArrayOutputStream()
      bufferedInputStream = new BufferedInputStream(inputStream)

      val length = bufferedInputStream.available()
      val bytes = new Array[Byte](length)
      if (bytes.size > 200) {
        //      val buffer = new StringBuffer()
        //      val encoder = new sun.misc.BASE64Encoder()

        var count = -1
        while ({
          count = inputStream.read(bytes, 0, length)
          count != -1
        }) {
          outputStream.write(bytes, 0, count)
        }
        val bytes_ = outputStream.toByteArray()

        //      val decoder = new sun.misc.BASE64Decoder()
        //      buffer.append(encoder.encode(bytes_))

        //      val base64Image = erase(buffer.toString(), "\n")
        //      println(base64Image)

        // 仅创建带page_id记录，与照片名page_id一致
        val source = com.sasaki.wp.persistence.poso.Source(id, "", "")
        source.imageName = imageName(id)
        saveSource(source)

        //生成jpeg图片
        val file = new File(fileName(id))
        if (file.exists())
          file.delete()
        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileName(id)));
        bufferedOutputStream.write(bytes_, 0, bytes_.length)
        bufferedOutputStream.flush()

        println(
          s"""
  Done!
  Request: ${urlImage(id)}
  Thread name: ${Thread.currentThread().getName}, process id: $id
""")
      }
    } catch {
      case t: Throwable => println(s"process error occur $id.\n" + t.printStackTrace())
    } finally {
      println(s"process $id get finally.\n ----------------------------------------------------")
      get.releaseConnection()
      if (null != inputStream)
        inputStream.close()
      if (null != bufferedOutputStream)
        bufferedOutputStream.close()
      if (null != outputStream)
        outputStream.close()
    }
  }
}

object ProcessUtil {
  val client = HttpClients.createDefault()

  val basePath = "http://www.virtualshanghai.net/"
  val urlContent = "http://www.virtualshanghai.net/Photos/Images?ID=1"
  // 部分imageName名称后边为No-01.jpeg，非No-1.jpeg
  val imageName = (id: Long) => s"dbImage_ID-${id}_No-1.jpeg"
  val urlImage = (id: Long) => s"http://www.virtualshanghai.net/Asset/Preview/${imageName(id)}"
  //  val url = "http://img6.3lian.com/c23/desk4/05/77/d/01.jpg"
  //  val url = "http://www.virtualshanghai.net/Asset/Preview/dbImage_ID-5_No-1.jpeg"
  val fileName = (id: Long) => s"/Users/sasaki/vsh/sh/${imageName(id)}"

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
}
