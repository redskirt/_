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
  val threadPool = Executors.newFixedThreadPool(25)
  
  val pathFile_ = "/Users/sasaki/git/_/web-digg/src/main/resources/harvard-yenching-pages.txt"
  val pathFile = "/Users/sasaki/git/_/web-digg/src/main/resources/harvard-yenching-json-results.txt"
  val pathThumbnail = "/Users/sasaki/git/doc/kj/harvard-yenching/thumbnail"
  val pathDefault = "/Users/sasaki/git/doc/kj/harvard-yenching/default"
  
  val buildOriginalJsonPage = (offset: Int) => s"https://images.hollis.harvard.edu/primo_library/libweb/webservices/rest/primo-explore/v1/pnxs?getMore=0&inst=01HVD&lang=en_US&limit=50&multiFacets=&offset=$offset&pcAvailability=true&q=any,contains,Hedda+Morrison+Photographs+of+China&qExclude=&qInclude=&rtaLinks=true&scope=default_scope&searchString=Hedda+Morrison+Photographs+of+China&sort=rank&tab=default_tab&vid=HVD_IMAGES"
  val buildTargetPage = (id: String) => s"https://nrs.harvard.edu/urn-3:FHCL:$id?buttons=Y"
  val buildRedirectJson = (id: String) => s"https://ids.lib.harvard.edu/ids/iiif/$id/info.json"
  val buildDefaultJpgPage = (id: String, maxWidth: Int) => s"https://ids.lib.harvard.edu/ids/iiif/$id/full/$maxWidth,/0/default.jpg"
		  
  implicit val formats = DefaultFormats 

  def main(args: Array[String]): Unit = {

    // 获取页面的Json，一条Json对应多个结果集（图片+内容）
//        val jsonFile =
//          {
//            for (i <- 16 until 18 /*119*/ ) yield {
//              val offset = i * 50 // 0 ~ 5900
//              val get = new HttpGet(buildOriginalJsonPage(offset))
//              get.setHeader("Host", "images.hollis.harvard.edu")
//              get.setHeader("Connection", "keep-alive")
//              get.setHeader("Cache-Control", "max-age=0")
//              get.setHeader("Upgrade-Insecure-Requests", "1")
//              get.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 get.setHeader(Safari/537.36")
//              get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//              get.setHeader("Accept-Encoding", "gzip, deflate, br")
//              get.setHeader("Accept-Language", "ja,en-US;q=0.9,en;q=0.8,zh-CN;q=0.7,zh;q=0.6")
//              get.setHeader("Authorization", "Bearer eyJraWQiOiJwcmltb0V4cGxvcmVQcml2YXRlS2V5LTAxSFZEIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJQcmltbyIsImp0aSI6IiIsImV4cCI6MTUzNzI1NDQyMCwiaWF0IjoxNTM3MTY4MDIwLCJ1c2VyIjoiYW5vbnltb3VzLTA5MTdfMDcwNzAwIiwidXNlck5hbWUiOm51bGwsInVzZXJHcm91cCI6IkdVRVNUIiwiYm9yR3JvdXBJZCI6bnVsbCwidWJpZCI6bnVsbCwiaW5zdGl0dXRpb24iOiIwMUhWRCIsInZpZXdJbnN0aXR1dGlvbkNvZGUiOiIwMUhWRCIsImlwIjoiNTguMzcuOTkuNDUiLCJwZHNSZW1vdGVJbnN0IjpudWxsLCJvbkNhbXB1cyI6ImZhbHNlIiwibGFuZ3VhZ2UiOiJlbl9VUyIsImF1dGhlbnRpY2F0aW9uUHJvZmlsZSI6IiIsInZpZXdJZCI6IkhWRF9JTUFHRVMiLCJpbHNBcGlJZCI6bnVsbCwic2FtbFNlc3Npb25JbmRleCI6IiJ9.LgdPXycyQSWJi8abNQaefgsSmY5DUtBTfDNTvzJNAKb2FgS552Aw6RF5x2WX6Xbhaxu5i4_sBgvprMmPltH69g")
//              get.setHeader("Cookie", "JSESSIONID=A6A67C783C30182920156F6F9D14EBCE; _ga=GA1.2.522930930.1536053308; sto-id-%3FDir-A_prod%3F01ORBIS.Prod.Primo.1701-sg=EIFIBMAK; sto-id-%3FDir-A_prod%3F01HVD.primo.for.alma.prod.1701-sg=LGFIBMAK; _gid=GA1.2.210371207.1537074884; JSESSIONID=51EC124B97289FB86842D343D681C894")
//    
//              val response = client.execute(get)
//    
//              val result = parseContent(response.getEntity.getContent)
////              println(result)
//              println(i)
//    
//              if(null != response)
//                response.close()
//              if(null != get)
//                get.completed()
//    
//              i + "\t" + result
//            }
//          } mkString ""
//          Util.writeFile(pathFile_, jsonFile)

    // 解析JSON子串，得出每个项的单一结果集
//    val lines =
//      Source.fromFile(new File(pathFile_), "utf-8")
//        .getLines()
//        .toSeq
//        
//    val result =
//      lines
////                .take(4)
////      {
////        for(i <- 15 until 17) yield lines(i)
////      }
//      .map { o =>
//          val array = o.split("\t")
//          val json = array(1).replace("\n", "")
//          //          println(json)
//          val jsons = (parse(json, false) \ "docs")
//          val listJsonStr = jsons
//            .children
//            .map { o =>
//              val `type` = //
//                o \ "@id" match {
//                  case t: JValue if (t.extract[String].contains("group")) => "group"
//                  case t: JValue if (t.extract[String].contains("work")) => "work"
//                  case _ => "_"
//                }
//              //              println(`type`)
//              array(0) + "\t" + `type` + "\t" + compact(render(o))
//            }
//          listJsonStr
//        }
//        .flatMap(identity)
//        .sortBy(_.split("\t")(0).toInt)
////        .foreach(println)
//        .mkString("\n")
//    Util.writeFile(pathFile, result)
    
    // 从单项结果集文件执行任务
    val lines =
      Source.fromFile(new File(pathFile), "utf-8")
        .getLines()
        .toSeq
//    println(lines.size) // 5914
        
    // 可直接下载图的单项 => work
    val linesWork = lines.filter(_.split("\t")(1).equals("work"))
        
    try {
      for (i <- 2017 until 2018 /*lines.size*/ ) {
        threadPool.execute(new DownloadImageProcess(i, linesWork(i)))
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
    get.setHeader("Authorization", "Bearer eyJraWQiOiJwcmltb0V4cGxvcmVQcml2YXRlS2V5LTAxSFZEIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJQcmltbyIsImp0aSI6IiIsImV4cCI6MTUzNzE2MTYyMSwiaWF0IjoxNTM3MDc1MjIxLCJ1c2VyIjoiYW5vbnltb3VzLTA5MTZfMDUyMDIxIiwidXNlck5hbWUiOm51bGwsInVzZXJHcm91cCI6IkdVRVNUIiwiYm9yR3JvdXBJZCI6bnVsbCwidWJpZCI6bnVsbCwiaW5zdGl0dXRpb24iOiIwMUhWRCIsInZpZXdJbnN0aXR1dGlvbkNvZGUiOiIwMUhWRCIsImlwIjoiNTguMzcuOTkuNDUiLCJwZHNSZW1vdGVJbnN0IjpudWxsLCJvbkNhbXB1cyI6ImZhbHNlIiwibGFuZ3VhZ2UiOiJlbl9VUyIsImF1dGhlbnRpY2F0aW9uUHJvZmlsZSI6IiIsInZpZXdJZCI6IkhWRF9JTUFHRVMiLCJpbHNBcGlJZCI6bnVsbCwic2FtbFNlc3Npb25JbmRleCI6IiJ9.hhc3SsXT-Lw2M9qXrI5GYyj9W4klNlDqY56jncQsac5NCfGeqOF3SnmwoCKK0o4sDmKWMkwFtkVDf6myipbNXQ")
    get.setHeader("Cookie", "JSESSIONID=D2674469246BD07C29428914F4B1779E; _ga=GA1.2.522930930.1536053308; sto-id-%3FDir-A_prod%3F01ORBIS.Prod.Primo.1701-sg=EIFIBMAK; sto-id-%3FDir-A_prod%3F01HVD.primo.for.alma.prod.1701-sg=LGFIBMAK; _gid=GA1.2.210371207.1537074884; JSESSIONID=51EC124B97289FB86842D343D681C894")
//    get.setHeader("If-None-Match", """W/"4636-1520771879000"""")
//    get.setHeader("If-Modified-Since", "Sun, 11 Mar 2018 12:37:59 GMT")
    
    val response = client.execute(get)
//    response.setHeader("ETag", "W/\"4636-1520771879000\"")
//    response.setHeader("Server", "Apache-Coyote/1.1")
//    response.setHeader("Date", "Tue, 11 Sep 2018 15:06:03 GMT")

    val result = parseContent(response.getEntity.getContent)
//    println(result)
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

  override def run(): Unit = {

    implicit val formats = DefaultFormats

    val array = line.split("\t")
    val page = array(0).toInt + 1
    val json = array(2)

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

    HttpDownload.download(thumbnail, s"$pathThumbnail/$page-${index + 1}-$workId-$redirectId-$fhclId.jpg")
    HttpDownload.download(urlDefaultPage, s"$pathDefault/$page-${index + 1}-$workId-$redirectId.jpg")
    println(s">> ========================== ${index + 1} DOWN! =================================")
  }
}