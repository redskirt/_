package com.sasaki.wp.sample

import com.sasaki.wp.persistence.QueryHelper
import org.jsoup.Jsoup
import scala.collection.mutable.ArrayBuffer
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.client.methods.HttpGet
import java.io.InputStream
import com.sasaki.wp.util.Util
import scala.io.Source
import java.io.File
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.net.URL
import com.sasaki.wp.util.HttpDownload
import java.util.concurrent.Executors

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Sep 8, 2018 11:05:24 AM
 * @Description 
 */
object WebDiggYenChing extends QueryHelper {
  
  val root = "https://images.hollis.harvard.edu"
  val client = HttpClients.createDefault()
  val threadPool = Executors.newFixedThreadPool(15)
  
//    val pathFile = "/Users/sasaki/git/_/web-digg/src/main/resources/harvard-yenching-pages.txt"
  val pathFile = "/Users/sasaki/git/_/web-digg/src/main/resources/harvard-yenching-json-results.txt"
  val pathThumbnail = "/Users/sasaki/git/doc/kj/harvard-yenching/thumbnail"
  val pathDefault = "/Users/sasaki/git/doc/kj/harvard-yenching/default"
		  
  implicit val formats = DefaultFormats 

  def main(args: Array[String]): Unit = {

    //    val jsonFile =
    //      {
    //        for (i <- 0 until 119) yield {
    //          val offset = i * 50 // 0 ~ 5900
    //          val get = new HttpGet(s"https://images.hollis.harvard.edu/primo_library/libweb/webservices/rest/primo-explore/v1/pnxs?getMore=0&inst=HVD&lang=en_US&limit=${if (5900 == offset) 14 else 50 /*最后一页仅14项*/ }&multiFacets=&offset=$offset&pcAvailability=true&q=any,contains,Hedda+Morrison+Photographs+of+China&qExclude=&qInclude=&rtaLinks=true&scope=default_scope&searchString=Hedda+Morrison+Photographs+of+China&sort=rank&tab=default_tab&vid=HVD_IMAGES")
    //          get.addHeader("Accept", "application/json, text/plain, */*")
    //          get.addHeader("Accept-Encoding", "gzip, deflate, br")
    //          get.addHeader("Authorization", "Bearer eyJraWQiOiJwcmltb0V4cGxvcmVQcml2YXRlS2V5LUhWRCIsImFsZyI6IkVTMjU2In0.eyJpc3MiOiJQcmltbyIsImp0aSI6IiIsImV4cCI6MTUzNjU4MzA5MCwiaWF0IjoxNTM2NDk2NjkwLCJ1c2VyIjoiYW5vbnltb3VzLTA5MDlfMTIzODEwIiwidXNlck5hbWUiOm51bGwsInVzZXJHcm91cCI6IkdVRVNUIiwiYm9yR3JvdXBJZCI6bnVsbCwidWJpZCI6bnVsbCwiaW5zdGl0dXRpb24iOiJIVkQiLCJ2aWV3SW5zdGl0dXRpb25Db2RlIjoiSFZEIiwiaXAiOiIxMTYuMjQ3LjEzMC44MCIsIm9uQ2FtcHVzIjoiZmFsc2UiLCJsYW5ndWFnZSI6ImVuX1VTIiwiYXV0aGVudGljYXRpb25Qcm9maWxlIjoiIiwidmlld0lkIjoiSFZEX0lNQUdFUyIsImlsc0FwaUlkIjpudWxsLCJzYW1sU2Vzc2lvbkluZGV4IjoiIn0.1kiSiWxm54NNsWEBBYa9bT-14_NRT6bTuA2MdlcGFOA4Ytu0Uyt3RDCR6fEQ_bMiCvayFvogXDElN7iNtOsdyg")
    //          get.addHeader("Connection", "keep-alive")
    //          get.addHeader("Cookie", "JSESSIONID=3E80D0B99F0F0E8056461D95245B6FB8; _ga=GA1.2.522930930.1536053308; sto-id-%3FDir-A_prod%3F01ORBIS.Prod.Primo.1701-sg=EIFIBMAK; _gid=GA1.2.324585732.1536496674")
    //          get.addHeader("Referer", "https://images.hollis.harvard.edu/primo-explore/search?query=any,contains,Hedda%20Morrison%20Photographs%20of%20China&tab=default_tab&search_scope=default_scope&vid=HVD_IMAGES&lang=en_US&offset=0&sortby=rank")
    //
    //          val response = client.execute(get)
    //          val result = parseContent(response.getEntity.getContent)
    //
    //          response.close()
    //          get.completed()
    //          println(i)
    //          i + "\t" + result
    //        }
    //      } mkString ""
    //    Util.writeFile(filePath, jsonFile)

    val lines = Source.fromFile(new File(pathFile), "utf-8").getLines().toSeq.sorted

    // 解析JSON子串，得出每个项的单一结果集
    //    val result = lines
    ////      .take(1)
    //      .map { o =>
    //        val array = o.split("\t")
    //        val json = array(1)
    //        val jsons = (parse(json) \ "docs")
    //        val listJsonStr = jsons
    //          .children
    //          .map { o =>
    //            array(0) + "\t" + compact(render(o))
    //          }
    //        listJsonStr
    //      }
    //      .flatMap(identity)
    //      .mkString("\n")
    //      Util.writeFile("", result)

    try {
      for (i <- 200 until 400 /*lines.size*/ ) {
        threadPool.execute(new DownloadImageProcess(i, lines(i)))
      }
    } finally
      threadPool.shutdown()
  }
  
  def parseFromTargetPage2RedirectPage(url: String) = {

    val get = new HttpGet(url)
    get.setHeader("Host", "images.hollis.harvard.edu")
    get.setHeader("Connection", "keep-alive")
    get.setHeader("Cache-Control", "max-age=0")
    get.setHeader("Upgrade-Insecure-Requests", "1")
    get.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 get.setHeader(Safari/537.36")
    get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    get.setHeader("Accept-Encoding", "gzip, deflate, br")
    get.setHeader("Accept-Language", "ja,en-US;q=0.9,en;q=0.8,zh-CN;q=0.7,zh;q=0.6")
    get.setHeader("Cookie", "_ga=GA1.2.522930930.1536053308; sto-id-%3FDir-A_prod%3F01ORBIS.Prod.Primo.1701-sg=EIFIBMAK")
    get.setHeader("If-None-Match", """W/"4636-1520771879000"""")
    get.setHeader("If-Modified-Since", "Sun, 11 Mar 2018 12:37:59 GMT")
    val response = client.execute(get)
    response.setHeader("ETag", "W/\"4636-1520771879000\"")
    response.setHeader("Server", "Apache-Coyote/1.1")
    response.setHeader("Date", "Tue, 11 Sep 2018 15:06:03 GMT")

    val result = parseContent(response.getEntity.getContent)
    val strContain_manifestUri = result
      .split("\n")
      .filter(_.contains("manifestUri"))(0)
      
    if(response != null)
      response.close()
      
    val manifestUri = (parse(strContain_manifestUri) \ "manifestUri").extract[String]
    manifestUri
  }
  
  def parseMaxWidthFromInfoJson(url: String) = {
    val get = new HttpGet(url)
    println("get: " + url)
    val response = client.execute(get)
    val json = parseContent(response.getEntity.getContent)
    val jsonO = parse(json)
    val maxWidth = (jsonO \ "maxWidth").extract[Int]
    maxWidth
  }
  
  def parseContent(input: java.io.InputStream): String = {
    import scala.io.Source
    val builder = StringBuilder.newBuilder
    Source.fromInputStream(input, "UTF-8").getLines().foreach(__ => builder.append(__).append("\n"))
    builder toString 
  }
}

class DownloadImageProcess(index: Int, line: String) extends Runnable {

  import WebDiggYenChing._

  val buildTargetPage = (id: String) => s"https://nrs.harvard.edu/urn-3:FHCL:$id?buttons=Y"
  val buildRedirectJson = (id: String) => s"https://ids.lib.harvard.edu/ids/iiif/$id/info.json"
  val buildDefaultJpgPage = (id: String, maxWidth: Int) => s"https://ids.lib.harvard.edu/ids/iiif/$id/full/$maxWidth,/0/default.jpg"

  override def run(): Unit = {

    implicit val formats = DefaultFormats

    val array = line.split("\t")
    val id = array(0)
    val json = array(1)

    val jsonObject = parse(json)
    val id_ = (jsonObject \ "@id").extract[String]

    val workId = id_.substring(id_.lastIndexOf("work") + 4)
    println(s"workId: $workId")

    // thumbnail page
    val thumbnail_ = (jsonObject \ "pnx" \ "links" \ "thumbnail")(0).extract[String]
    val thumbnail = thumbnail_.substring(3)
    println(s"thumbnail page: $thumbnail")

    // content page
    val fhclId = thumbnail.substring(thumbnail.indexOf("FHCL:") + 5, thumbnail.indexOf("?"))
    println(s"fhclId: $fhclId")

    // target page
    // https://nrs.harvard.edu/urn-3:FHCL:5011700?buttons=Y
    val targetPage = buildTargetPage(fhclId)
    // val targetPage = "https://nrs.harvard.edu/urn-3:FHCL:5011700?buttons=Y"
    println(s"target page: $targetPage")

    val redirectPage = parseFromTargetPage2RedirectPage(targetPage)
    println(s"redirect page: $redirectPage")

    val redirectId = redirectPage.substring(redirectPage.indexOf("ids:") + 4, redirectPage.length())
    println(s"redirect id: $redirectId")

    val urlInfoJson = buildRedirectJson(redirectId)
    val maxWidth = parseMaxWidthFromInfoJson(urlInfoJson)
    val urlDefaultPage = buildDefaultJpgPage(redirectId, maxWidth)
    println(s"default page: $urlDefaultPage")

    HttpDownload.download(thumbnail, s"$pathThumbnail/$id-$index-$workId-$redirectId-$fhclId.jpg")
    HttpDownload.download(urlDefaultPage, s"$pathDefault/$id-$index-$workId-$redirectId.jpg")
    println(s">> ========================== $index DOWN! =================================")
  }
}