package com.sasaki.wp.sample

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.jsoup.Jsoup

import com.sasaki.wp.persistence.QueryHelper
import com.sasaki.wp.util.NetStreamIOHandler
import java.net.URL
import java.io.File

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 13, 2018 10:30:32 PM
 * @Description
 */

object VirtualShanghaiWebDigg extends QueryHelper {
//  val threadPool = Executors.newFixedThreadPool(1)
  
  import ProcessUtil._
  
  def main(args: Array[String]): Unit = {
    /**
     * => 多线程任务
     */
    //    try {
    //      /**
    //       * 图片抓取
    //       */
    //      //      for (i <- 23326 to 30000)
    //      //        threadPool.execute(new ImageFetchProcess(i))
    //
    //      /**
    //       * 页面抓取
    //       */
    //      /**
    //       * 0 1 2 3 4 5 6 7
    //       * 1 2 3 4 5 7 6 8
    //       */
    //      //      val pageIds = listPageId
    //      //      println(pageIds.indexOf(17556))
    //      //      for (i <- 4001 until pageIds.size)
    //      //        threadPool.execute(new ContentFetchProcess(pageIds(i)))
    //
    //    } finally
    //      threadPool.shutdown()

    /**
     * => 常规任务
     */

    /**
     * 页面清洗
     */
    //      source2ViewProcess

    val html = scala.io.Source.fromURL("https://mp.weixin.qq.com/mp/profile_ext?action=getmsg&__biz=MzI2MTM2MTIwOQ==&f=json&offset=20&count=10&is_ok=1&scene=124&uin=777&key=777&pass_ticket=&wxtoken=&appmsg_token=957_aHWsQD3pwQQ84p6thk-KWOHVUraDd4dGbPibaw~~&x5=0&f=json")
    
  }  
  
  
  
  /**
   * 指定单个微信页面，抓取当前页面所有图片
   */
  def downloadFileFromPageProcess(url: String) = {
    val result = fetchImageUrlFromPage(url)
    val title = result._1
    val urls = result._2
    val dir = s"/Users/sasaki/KJ/wx/$title"
    val file = new File(dir)
    if(!file.exists())
      file.mkdir()
    for(i <- 0 until urls.size)  {
      println("url: " + i + ", " + urls(i))
      NetStreamIOHandler(urls(i), s"$dir/$i.jpeg").download
    }
  }

  def source2ViewProcess = {
    val contents = listContent
    contents.foreach { o =>
      val pageId = o._1
      val html = o._2
      val document = Jsoup.parse(html)
      val table = document.getElementsByClass("defaultDocTable")
      val trs = table.select("tr")

      val map = {
        for (i <- 0 until trs.size()) yield {
          val tr = trs.get(i)
          val tds = tr.select("td")
          val columnName = tds.first().text()
          val columnText = tds.last().getElementsByClass("defaultDocTableContent")

          val ul = columnText.select("ul")
          val textOrUl =
            if (ul.isEmpty())
              columnText.text()
            else {
              val lis = ul.select("li")
              if (!lis.isEmpty())
                { //
                  for (j <- 0 until lis.size()) yield {
                    val li = lis.get(j)
                    val href_text = li.selectFirst("a[href]")
                    val href = ProcessUtil.basePath + href_text.attr("href")
                    val text = href_text.text()
                    (text, href)
                  }
                } map {
                  case (text, href) => //
                    s"$text, Reference: $href"
                } mkString (" | ")
              else ""
            }
          (columnName, textOrUl)
        }
      } toMap

      val shView = com.sasaki.wp.persistence.poso.ShView(
        pageId,
        pageId,
        map.getOrElse("Title", ""),
        map.getOrElse("Collection", ""),
        map.getOrElse("Location", ""),
        map.getOrElse("Year", ""),
        map.getOrElse("Date", ""),
        map.getOrElse("Estimated date", ""),
        map.getOrElse("Image type", ""),
        map.getOrElse("Material form of image", ""),
        map.getOrElse("Private Repository", ""),
        map.getOrElse("Note(s)", ""),
        map.getOrElse("Keyword(s) [en]", ""),
        map.getOrElse("Keyword(s) [fr]", ""),
        map.getOrElse("Street name", ""),
        map.getOrElse("Repository", ""),
        map.getOrElse("Building", ""),
        map.getOrElse("Related Image", ""))
      saveShView(shView)
    }
  }
}

class ContentFetchProcess(pageId: Long) extends Runnable with QueryHelper {
  import ProcessUtil._
  import com.sasaki.wp.persistence.poso._

  override def run() {
    val get = new HttpGet(urlContent(pageId))
    val response = client.execute(get)

    try {
      val pageHtml = parseResponse(response)
      updateContent(Source(pageId, pageHtml, ""))
      println(
        s"""
  Done!
  Request: ${urlContent(pageId)}
  Thread name: ${Thread.currentThread().getName}, process id: $pageId
""")
    } catch {
      case t: Throwable => t.printStackTrace()
    } finally {
      if (null != get)
        get.releaseConnection()
    }
  }

}

class ImageFetchProcess(id: Int) extends Runnable with QueryHelper {

  import ProcessUtil._

  override def run() {
    try {
      NetStreamIOHandler(urlImage(id), fileName(id)).download

      // 仅创建带page_id记录，与照片名page_id一致
      val source = com.sasaki.wp.persistence.poso.Source(id, "", "")
      source.imageName = imageName(id)
      saveSource(source)

      println(
        s"""
  Done!
  Request: ${urlImage(id)}
  Thread name: ${Thread.currentThread().getName}, process id: $id
""")
    } catch {
      case t: Throwable => println(s"process error occur $id.\n" + t.printStackTrace())
    } finally {
      println(s"process $id get finally.\n ----------------------------------------------------")
    }
  }
}

object ProcessUtil {
  val client = org.apache.http.impl.client.HttpClients.createDefault()
  val basePath = "http://www.virtualshanghai.net"
  val urlContent = (id: Long) => s"http://www.virtualshanghai.net/Photos/Images?ID=$id"
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

  /**
   * 抓取当前页面图片地址
   * 仅适用微信公众号页面
   */
  def fetchImageUrlFromPage(url: String) = {
    val get = new HttpGet(url)
    try {
      val document = Jsoup.parse(new URL(url), 10000)
      val imgs = document.select("img[data-src]")
      val response: HttpResponse = ProcessUtil.client.execute(get)
      val urls = for (i <- 0 until imgs.size()) yield imgs.get(i).attr("data-src")
      val line = document.html().lines.filter(_.contains("msg_title")).toArray.head
      val title = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""))
      (title, urls)
    } finally {
      if (null != get)
        get.releaseConnection()
    }
  }
  
  /**
   * 抓取当前页面列表地址
   * 仅适用微信公众号页面
   */
  def fetchPageUrlFromListPage() = {
    
  }
}


//      val buffer = new StringBuffer()
//      val encoder = new sun.misc.BASE64Encoder()
//      val decoder = new sun.misc.BASE64Decoder()
//      buffer.append(encoder.encode(bytes_))

//      val base64Image = erase(buffer.toString(), "\n")
//      println(base64Image)
