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

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Sep 8, 2018 11:05:24 AM
 * @Description 
 */
object WebDiggYenChing extends QueryHelper {
  
  val root = "https://images.hollis.harvard.edu"
  lazy val client = HttpClients.createDefault()
		  
  implicit val formats = DefaultFormats 

  def main(args: Array[String]): Unit = {
    
    val filePath = "/Users/sasaki/git/_/web-digg/src/main/resources/harvard-yenching-pages.txt"
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
    
    val lines = Source.fromFile(new File(filePath), "utf-8").getLines()
    val buildTargetPage = (id: String) => s"$root/primo-explore/fulldisplay?vid=HVD_IMAGES&search_scope=default_scope&tab=default_tab&docid=HVD_VIAolvwork$id&sortby=rank&offset=0"
    lines
      .take(1)
      .map { o =>
        val array = o.split("\t")
        (array(0), array(1))
      }
      .foreach { o =>
        val id = o._1
        val json = o._2
        val jsonObject = parse(json)
        val docs = jsonObject \ "docs"
        docs
        .children
        .take(1)
        .foreach { o_ =>
          val thumbnail_ = (o_ \ "pnx" \ "links" \ "thumbnail")(0).extract[String]
          val thumbnail = thumbnail_.substring(3)
          val url = (o_ \ "@id").extract[String]
          val workId = url.substring(url.lastIndexOf("work") + 4)
          val targetPage = buildTargetPage(workId)

          println(thumbnail + " " + targetPage)
          parseFromTargetPage(targetPage)
          
        }
      }
  }
  
  private def parseFromTargetPage(url: String) = {
    
    val get = new HttpGet(url)
    get.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    get.addHeader("Connection", "keep-alive")
    get.addHeader("Cookie", "_ga=GA1.2.522930930.1536053308; sto-id-%3FDir-A_prod%3F01ORBIS.Prod.Primo.1701-sg=EIFIBMAK; _gid=GA1.2.324585732.1536496674")
    get.addHeader("If-Modified-Since", "Sun, 11 Mar 2018 12:37:59 GMT")
    get.addHeader("If-None-Match", "W/\"4636-1520771879000\"")
    val response = client.execute(get)
    response.addHeader("ETag", "W/\"4636-1520771879000\"")
    response.addHeader("Server", "Apache-Coyote/1.1")
    response.addHeader("Date", "Mon, 10 Sep 2018 10:04:26 GMT")
    val result = parseContent(response.getEntity.getContent)
    println(result)
    
//    val document = Jsoup.parse(new URL(url), 600000)
//    println(document)
//    val item_details = document.getElementById("item-details")
//    val flexs = item_details.getElementsByClass("flex")
//    for(i <- 0 until flexs.size()) {
//      val flex = flexs.get(i)
//      val span = flex.getElementsByTag("span").first()
//      println(span.text())
//    }
    
    
  }
  
  private def parseContent(input: java.io.InputStream): String = {
    import scala.io.Source
    val builder = StringBuilder.newBuilder
    Source.fromInputStream(input, "UTF-8").getLines().foreach(__ => builder.append(__).append("\n"))
    builder toString 
  }
}
