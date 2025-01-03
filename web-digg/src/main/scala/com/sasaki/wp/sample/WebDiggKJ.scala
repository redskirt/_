package com.sasaki.wp.sample

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.jsoup.Jsoup

import com.sasaki.wp.persistence.QueryHelper
import com.sasaki.wp.util.NetStreamIOHandler
import java.net.URL
import java.io.File
import com.sasaki.wp.persistence.poso.Source
import java.util.concurrent.Executors
import com.sasaki.wp.util.Util

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 13, 2018 10:30:32 PM
 * @Description
 */

object WebDiggKJ extends QueryHelper {
  val threadPool = Executors.newFixedThreadPool(1)
  
  import ProcessUtil._
  
  def main(args: Array[String]): Unit = {
    /**
     * => 多线程任务
     */
//		                  try {
//		        /**
//		         * 图片抓取
//		         */
//              val pageExists = Util.listFiles("/Users/sasaki/vsh/map/BJG")
//                .filter(_.getName.contains("dbImage"))
//                .map { o =>
//                  val name = o.getName
//                  println(name)
//                  println(name.substring(11, name.lastIndexOf("_")))
//                  name.substring(11, name.lastIndexOf("_")).toLong
//                }.toSet
//
//              val pages = listPageId("BJG", "map").toSet
//              val pages_ = pages -- pageExists
//              for(i <- 0 until pages_.size)
//                threadPool.execute(new ImageFetchProcess(pages_.toList(i).toInt))
		  
		        /**
		         * 页面抓取
		         */
		        /**
		         * 0 1 2 3 4 5 6 7
		         * 1 2 3 4 5 7 6 8
		         */
//            val pageIds =  listPageId("TJN", "map") // pages_ 
//            for (i <- 0 until Array(1449).size /*pageIds.size*/)
//              threadPool.execute(new ContentFetchProcess(pageIds(i)))
		  
		        /**
		         * @Deprecated
		         * 更新Base64至Source表
		         */
//		        val files = Util.listFiles("/Users/sasaki/vsh/bj")
//		          .filter(_.getName.contains("dbImage"))
//		  
//		        for (i <- 1100 until files.size)
//		          threadPool.execute(new File2Base64Process(files(i), "bj"))
//		                  } finally
//		                    threadPool.shutdown()
    /**
     * => 常规任务
     */

    /**
     * 页面清洗
     */
//          source2ViewMapProcess("HKU", "map")

//        val html = scala.io.Source.fromURL("https://mp.weixin.qq.com/mp/profile_ext?action=getmsg&__biz=MzI2MTM2MTIwOQ==&f=json&offset=20&count=10&is_ok=1&scene=124&uin=777&key=777&pass_ticket=&wxtoken=&appmsg_token=957_aHWsQD3pwQQ84p6thk-KWOHVUraDd4dGbPibaw~~&x5=0&f=json")

    /**
     * 下载指定微信页面所有图片
     * downloadFileFromPageProcess("")
     */
//        downloadFileFromPageProcess("https://mp.weixin.qq.com/s/UDYXMQJi1ZrcDESLKoMR2g")

    /**
     * 获取列表页面所有Image Id
     */
//            for(i <- 1 to 1)
//              saveListPageSource(i, "TJN", "map")

    /**
     * 删除废照片
     *
     */
//        Util.listFiles("/Users/sasaki/vsh/map/SHI")
//          .filter(_.getName.contains("dbImage"))
//          .foreach { o =>
//            if (200 > o.length()) {
//              println(o.getName)
//              o.delete()
//            }
//          }

    /**
     * 图片重新编号
     */
    
//    val city = "HKU"
//    val list = Util.listFiles(s"/Users/sasaki/vsh/map/$city")
//      .filter(_.getName.contains("dbImage"))
//      .map { o =>
//        val name = o.getName
//        val imageId = name.substring(11, name.lastIndexOf("_")).toLong
//        (imageId, o)
//      }.sortBy(o => o._1)
//      
//      println(list.size)
//      
//    for (i <- 0 until list.size) {
//      val imageId = list(i)._1
//  		  val file = list(i)._2
//      val imageId_ = city + String.format("%04d", (i+1).asInstanceOf[Integer])
//      val source = Source(imageId, `city`, "map")
//      source.imageId = imageId_
//      updateSource(source)
//      println(i)
////      println(file.renameTo(new File(s"/Users/sasaki/vsh/map/${city}_/$imageId_.jpg")))
//    }
    
//     Util.listFiles(s"/Users/sasaki/vsh/city/TJN")
////     .take(1)
//     .foreach { o =>
//       val name = o.getName.substring(0, 7)
//       val name_ = name.replace("TJG", "TJN")
//       println(name)
//       o.renameTo(new File(s"/Users/sasaki/vsh/city/TJN/$name_.jpg"))
//     }
    
   val urls = parseLocalTitleListPageProcess("/Users/sasaki/Desktop/tc.html")
   for(i <- 155 until urls.size)  {
     threadPool.execute(new DownloadResourcesFromWXProcess(i, urls(i), true))
	   
//     downloadFileFromPageProcess(urls(i), true)
     }
//      .foreach(o => downloadFileFromPageProcess(o, true))
//        .foreach(o => downloadTextFromPageProcess(o))      
        
  }  
  
  def saveListPageSource(page: Int, `city`: String, `type`: String) = {
    val document = Jsoup.parse(new URL(urlLisTj_map(page)), 30000)
    val table = document.getElementsContainingText("Document ID"/*"Estimated date"*/)
    val trs = table.select("tr")
    println("page: " + page + ", size: " + trs.size()) // 204

    if(trs.size() > 2) {
      for (i <- 1 until trs.size()) yield {
        val td = trs.get(i).select("td")
        if (!td.isEmpty() && null != td && td.size() > 1) {
          val id = td.get(1).text()
          if ("" != id) id.toLong else 0
        } else
          0
      }
    } filter(_ != 0) foreach { o => 
      saveSource(Source(o, city, `type`))
      println(s"saved: $o")
    }
  }
  
  /** 
   * 解析微信标题列表页面
   */
  def parseLocalTitleListPageProcess(url: String): Seq[String] = {
    val file = new File(url)
    val document = Jsoup.parse(file, "utf-8")
    val js_history_list = document.getElementById("js_history_list")
    val list = js_history_list.getElementsByClass("weui_msg_card")
    for(i <- 0 until list.size()) yield {
      val o = list.get(i)
      val weui_msg_card_bd = o.child(1)
      val weui_media_box = weui_msg_card_bd.child(0)
      weui_media_box.attr("hrefs")
    }
  }
  
  /**
   * 指定单个微信页面，抓取当前页面文本
   */
  def downloadTextFromPageProcess(url: String) = {
    val document = Jsoup.parse(new URL(url), 10000)
    val content = document.getElementById("js_content")
    val result = fetchImageUrlFromPage(url)
    val title = result._1.replace(' ', '_').replace('/', '_').replace('\\', '_') + ".txt"
    val listp = content.getElementsByTag("p")

    val output =
      {
        for (i <- 0 until listp.size()) yield { listp.get(i).text() }
      } mkString("\n")
    
    Util.writeFile("/Users/sasaki/KJ/wx/nt/" + title, output)
  }
  
  /**
   * 指定单个微信页面，抓取当前页面所有图片
   */
  def downloadFileFromPageProcess(url: String, fetchText: Boolean = false) = {
    val rootDir = "/Users/sasaki/git/doc/kj/wx/tc"
    val result = fetchImageUrlFromPage(url)
    val title = result._1.replace(' ', '_').replace('/', '_').replace('\\', '_')
    val urls = result._2
    val dir = s"$rootDir/$title"
    val file = new File(dir)
    if(!file.exists())
      file.mkdir()
    for(i <- 0 until urls.size)  {
      println("url: " + i + ", " + urls(i))
      NetStreamIOHandler(urls(i), s"$dir/$i.jpg") download
    }
    
    if(fetchText) {
      val listp = result._3.getElementById("js_content").getElementsByTag("p")
      val output =
        {
          for (i <- 0 until listp.size()) yield { listp.get(i).text() }
        } mkString("\n")
      
      Util.writeFile(s"$rootDir/$title/$title.txt", output)
    }
  }

//  def source2ViewProcess(city: String) = {
//    val contents = listContent(city) //take(10)
//    contents.foreach { o =>
//      val pageId = o._1
//      val html = o._2
//      val imageId = o._3
//      val document = Jsoup.parse(html)
//      val table = document.getElementsByClass("defaultDocTable")
//      val trs = table.select("tr")
//
//      var photographer: String = ""
//      
//      val map = {
//        for (i <- 0 until trs.size()) yield {
//          val tr = trs.get(i)
//          val tds = tr.select("td")
//          val columnName = tds.first().text()
//          val columnText = tds.last().getElementsByClass("defaultDocTableContent")
//
//          if("Photographer" == columnName) {
//            val href_text = columnText.select("a[href]").first()
//            val href = ProcessUtil.basePath + href_text.attr("href")
//            val text = href_text.text()
//            photographer = s"$text, Reference: $href"
//          } 
//            
//          val ul = columnText.select("ul")
//          val textOrUl =
//            if (ul.isEmpty())
//              columnText.text()
//            else {
//              val lis = ul.select("li")
//              if (!lis.isEmpty()) { //
//                  for (j <- 0 until lis.size()) yield {
//                    val li = lis.get(j)
//                    val href_text = li.selectFirst("a[href]")
//                    val href = ProcessUtil.basePath + href_text.attr("href")
//                    val text = href_text.text()
//                    (text, href)
//                  }
//                } map {
//                  case (text, href) => //
//                    s"$text, Reference: $href"
//                } mkString (" | ")
//              else ""
//            }
//          (columnName, textOrUl)
//        }
//      } toMap
//      
//      val view = new com.sasaki.wp.persistence.poso.View
//      view.pageId = pageId
//      view.imageId = imageId
//      view.city = city
//      view.title = map.getOrElse("Title", "")
//      view.collection = map.getOrElse("Collection", "")
//      view.location = map.getOrElse("Location", "") 
//      view.extent = map.getOrElse("Extent", "")
//      view.year = map.getOrElse("Year", "")
//      view.date = map.getOrElse("Date", "")
//      view.photographer = photographer
//      view.estimatedDate = map.getOrElse("Estimated date", "")
//      view.imageType = map.getOrElse("Image type", "")
//      view.materialFormOfImage = map.getOrElse("Material form of image", "")
//      view.privateRepository = map.getOrElse("Private Repository", "")
//      view.notes = map.getOrElse("Note(s)", "")
//      view.keywordsEn = map.getOrElse("Keyword(s) [en]", "")
//      view.keywordsFr = map.getOrElse("Keyword(s) [fr]", "")
//      view.streetName = map.getOrElse("Street name", "")
//      view.repository = map.getOrElse("Repository", "")
//      view.building = map.getOrElse("Building", "")
//      view.relatedImage = map.getOrElse("Related Image", "")
//      
//      saveView(view)
//    }
//  }
  
  def source2ViewMapProcess(city: String, `type`: String) = {
    val contents = listContent(city, `type`) //take(10)
    contents.foreach { o =>
      val pageId = o._1
      val html = o._2
      val imageId = o._3
      val document = Jsoup.parse(html)
      val table = document.getElementsByClass("defaultDocTable")
      val trs = table.select("tr")

      var photographer: String = ""
      
      val map = {
        for (i <- 0 until trs.size()) yield {
          val tr = trs.get(i)
          val tds = tr.select("td")
          val columnName = tds.first().text()
          val columnText = tds.last().getElementsByClass("defaultDocTableContent")

          if("Photographer" == columnName) {
            val href_text = columnText.select("a[href]").first()
            val href = ProcessUtil.basePath + href_text.attr("href")
            val text = href_text.text()
            photographer = s"$text, Reference: $href"
          } 
            
          val ul = columnText.select("ul")
          val textOrUl =
            if (ul.isEmpty())
              columnText.text()
            else {
              val lis = ul.select("li")
              if (!lis.isEmpty()) { //
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
      
      val view = new com.sasaki.wp.persistence.poso.ViewMap
      view.page_id = pageId
      view.image_id = imageId
      view.city = city
      view.original_title = map.getOrElse("Original title", "")
      view.transliteration = map.getOrElse("Transliteration", "") 
      view.alternative_orivinal_title = map.getOrElse("Alternative original title", "") 
      view.collection = map.getOrElse("Collection", "")
      view.digtized_file = map.getOrElse("Digitized file", "")
      view.map_type = map.getOrElse("Map type", "")
      view.authors = map.getOrElse("Author(s)", "")
      view.year = map.getOrElse("Year", "")
      view.size = map.getOrElse("Size", "")
      view.map_support = map.getOrElse("Map support", "")
      view.place_of_publication = map.getOrElse("Place of publication", "")
      view.repository = map.getOrElse("Repository", "")
      view.publishers = map.getOrElse("Publisher(s)", "")
      saveViewMap(view)
    }
  }
}

class File2Base64Process(file: File, `city`: String) extends Runnable with QueryHelper {

  override def run() {
    val name = file.getName
    val id = name.substring(11, name.lastIndexOf("_")).toLong
    val base64 = NetStreamIOHandler.compileBase64Code(file)
    val source = Source(id, `city`, "")
    updateSource(source)
    println(
    s"""
  Done!
  Thread name: ${Thread.currentThread().getName}, process id: $name
  """    
    )
  }
}

class ContentFetchProcess(pageId: Long) extends Runnable with QueryHelper {
  import ProcessUtil._
  import com.sasaki.wp.persistence.poso._

  override def run() {
    val get = new HttpGet(urlContentTj_map(pageId))
    val response = client.execute(get)

    try {
      val pageHtml = parseResponse(response)
//      println(Source(pageId, pageHtml, "bj"))
      val source = Source(pageId, "TJN", "map")
      source.content = pageHtml
      updateSource(source) 
      println(
        s"""
  Done!
  Request: ${urlContentHk_map(pageId)}
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
      val url = urlImageBj_map(id)
      println(s"Downloading page url: $url")
      NetStreamIOHandler(url, fileName_map(id, "BJG")).download

      // 仅创建带page_id记录，与照片名page_id一致
//      val source = Source(id, "", "")
//      source.imageName = imageName_map(id)
//      saveSource(source)

      println(
        s"""
  Done!
  Request: ${urlImageBj_map(id)}
  Thread name: ${Thread.currentThread().getName}, process id: $id
""")
    } catch {
      case t: Throwable => println(s"process error occur $id.\n" + t.printStackTrace())
    } finally {
      println(s"process $id get finally.\n ----------------------------------------------------")
    }
  }
}

class DownloadResourcesFromWXProcess(i:Int, url: String, fetchText: Boolean) extends Runnable {
  
  override def run(): Unit = {
    WebDiggKJ.downloadFileFromPageProcess(url, fetchText)
    println(s"i: $i")
  }
}

object ProcessUtil {
  val client = org.apache.http.impl.client.HttpClients.createDefault()
  val basePath = "http://www.virtualshanghai.net"
  
  val urlContentSh = (id: Long) => s"http://www.virtualshanghai.net/Photos/Images?ID=$id"
  val urlContentTj = (id: Long) => s"http://tianjin.virtualcities.fr/Photos/Images?ID=$id"
  val urlContentHk = (id: Long) => s"http://hankou.virtualcities.fr/Photos/Images?ID=$id"
  val urlContentBj = (id: Long) => s"http://beijing.virtualcities.fr/Photos/Images?ID=$id"
  val urlContentSz = (id: Long) => s"http://suzhou.virtualcities.fr/Photos/Images?ID=$id"

  val urlContentSh_map = (id: Long) => s"http://www.virtualshanghai.net/Maps/Collection?ID=$id"
  val urlContentBj_map = (id: Long) => s"http://beijing.virtualcities.fr/Maps/Collection?ID=$id"
  val urlContentHk_map = (id: Long) => s"http://hankou.virtualcities.fr/Maps/Collection?ID=$id"
  val urlContentTj_map = (id: Long) => s"http://tianjin.virtualcities.fr/Maps/Collection?ID=$id"
  val urlContentSz_map = (id: Long) => s"http://suzhou.virtualcities.fr/Maps/Collection?ID=$id"
  
  // 部分imageName名称后边为No-01.jpeg，非No-1.jpeg
  val imageName = (id: Long) => s"dbImage_ID-${id}_No-2.jpeg"
  val imageName_map = (id: Long) => s"vcMap_ID-${id}_No-1.jpeg"
  
  val fileName = (id: Long, `city`: String) => s"/Users/sasaki/vsh/${`city`}/${imageName(id)}"
  val fileName_map = (id: Long, `city`: String) => s"/Users/sasaki/vsh/map/${`city`}/${imageName(id)}"
  
  val urlImageSh = (id: Long) => s"http://www.virtualshanghai.net/Asset/Preview/${imageName(id)}"
  val urlImageBj = (id: Long) => s"http://beijing.virtualcities.fr/Asset/Preview/${imageName(id)}"
  val urlImageHk = (id: Long) => s"http://hankou.virtualcities.fr/Asset/Preview/${imageName(id)}"
  val urlImageSz = (id: Long) => s"http://suzhou.virtualcities.fr/Asset/Preview/${imageName(id)}"
  val urlImageTj = (id: Long) => s"http://tianjin.virtualcities.fr/Asset/Preview/${imageName(id)}"
  
  val urlImageSh_map = (id: Long) => s"http://www.virtualshanghai.net/Asset/Preview/${imageName_map(id)}"
  val urlImageBj_map = (id: Long) => s"http://beijing.virtualcities.fr/Asset/Preview/${imageName_map(id)}"
  val urlImageHk_map = (id: Long) => s"http://hankou.virtualcities.fr/Asset/Preview/${imageName_map(id)}"
  val urlImageSz_map = (id: Long) => s"http://suzhou.virtualcities.fr/Asset/Preview/${imageName_map(id)}"
  val urlImageTj_map = (id: Long) => s"http://tianjin.virtualcities.fr/Asset/Preview/${imageName_map(id)}"

  
  // 列表页面
  val urlLisSh = (page: Int) => s"http://www.virtualshanghai.net/Photos/Images?pn=$page&rp=100"
  val urlLisBj = (page: Int) => s"http://beijing.virtualcities.fr/Photos/Images?pn=$page&rp=100"
  val urlLisHk = (page: Int) => s"http://hankou.virtualcities.fr/Photos/Images?pn=$page&rp=100"
  val urlLisSz = (page: Int) => s"http://suzhou.virtualcities.fr/Photos/Images?pn=$page&rp=100"
  val urlLisTj = (page: Int) => s"http://tianjin.virtualcities.fr/Photos/Images?pn=$page&rp=100"

  // 列表页面、地图
  val urlLisSh_map = (page: Int) => s"http://www.virtualshanghai.net/Maps/Collection?pn=$page&rp=100"
  val urlLisBj_map = (page: Int) => s"http://beijing.virtualcities.fr/Maps/Collection?pn=$page&rp=100"
  val urlLisHk_map = (page: Int) => s"http://hankou.virtualcities.fr/Maps/Collection?pn=$page&rp=100"
  val urlLisSz_map = (page: Int) => s"http://suzhou.virtualcities.fr/Maps/Collection?pn=$page&rp=100"
  val urlLisTj_map = (page: Int) => s"http://tianjin.virtualcities.fr/Maps/Collection?pn=$page&rp=100"
  
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
      val line = document.html().lines.filter(_.contains("msg_title")).toArray.headOption.getOrElse("\"no have title\"")
      val title = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""))
      (title, urls, document)
    } finally {
      if (null != get)
        get.releaseConnection()
    }
  }
  
}
