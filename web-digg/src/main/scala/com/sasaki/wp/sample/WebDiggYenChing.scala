package com.sasaki.wp.sample

import java.io.File
import java.util.concurrent.Executors

import scala.io.Source
import scala.xml.XML

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jvalue2extractable
import org.json4s.jvalue2monadic
import org.json4s.string2JsonInput

import com.sasaki.wp.persistence.QueryHelper
import com.sasaki.wp.util.HttpDownload
import scala.util.Try
import scala.util.Failure
import scala.util.Success

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Sep 8, 2018 11:05:24 AM
 * @Description
 */
object WebDiggYenChing extends QueryHelper {
  
  val __ = "_"

  val root = "https://images.hollis.harvard.edu"
  val client = HttpClients.createDefault()
  val threadPool = Executors.newFixedThreadPool(1)

  val pathPageFile = "/Users/sasaki/git/_/web-digg/src/main/resources/harvard-yenching-pages2.txt"
  val pathFile = "/Users/sasaki/git/_/web-digg/src/main/resources/harvard-yenching-json-results2.txt"
  val pathThumbnail = "/Users/sasaki/git/doc/kj/harvard-yenching/thumbnail"
  val pathDefault = "/Users/sasaki/git/doc/kj/harvard-yenching/default"
  val pathSh = "/Users/sasaki/git/doc/kj/harvard-yenching/sh"
  
  /**
   * Hedda Morrison
   */
  // https://images.hollis.harvard.edu/primo_library/libweb/webservices/rest/primo-explore/v1/pnxs?getMore=0&inst=01HVD&lang=en_US&limit=50&multiFacets=&offset=0&pcAvailability=true&q=any,contains,Hedda+Morrison+Photographs+of+China&qExclude=&qInclude=&rtaLinks=true&scope=default_scope&searchString=Hedda+Morrison+Photographs+of+China&sort=rank&tab=default_tab&vid=HVD_IMAGES
//  val buildOriginalJsonPage = (offset: Int) => s"https://images.hollis.harvard.edu/primo_library/libweb/webservices/rest/primo-explore/v1/pnxs?getMore=0&inst=01HVD&lang=en_US&limit=50&multiFacets=&offset=$offset&pcAvailability=true&q=any,contains,Hedda+Morrison+Photographs+of+China&qExclude=&qInclude=&rtaLinks=true&scope=default_scope&searchString=Hedda+Morrison+Photographs+of+China&sort=rank&tab=default_tab&vid=HVD_IMAGES"
  
  /**
   * 带关键字 上海
   */
  // https://images.hollis.harvard.edu/primo_library/libweb/webservices/rest/primo-explore/v1/pnxs?getMore=0&inst=01HVD&lang=en_US&limit=50&multiFacets=&offset=100&pcAvailability=true&q=any,contains,shanghai&qExclude=&qInclude=&rtaLinks=true&scope=default_scope&searchString=shanghai&sort=rank&tab=default_tab&vid=HVD_IMAGES
  // https://images.hollis.harvard.edu/primo_library/libweb/webservices/rest/primo-explore/v1/pnxs?getMore=0&inst=01HVD&lang=en_US&limit=50&multiFacets=&offset=0&pcAvailability=true&q=any,contains,shanghai&qExclude=&qInclude=&rtaLinks=true&scope=default_scope&searchString=shanghai&sort=rank&tab=default_tab&vid=HVD_IMAGES
  val buildOriginalJsonPage = (offset: Int) => s"https://images.hollis.harvard.edu/primo_library/libweb/webservices/rest/primo-explore/v1/pnxs?getMore=0&inst=01HVD&lang=en_US&limit=50&multiFacets=&offset=$offset&pcAvailability=true&q=any,contains,shanghai&qExclude=&qInclude=&rtaLinks=true&scope=default_scope&searchString=shanghai&sort=rank&tab=default_tab&vid=HVD_IMAGES"
  
  val buildTargetPage = (id: String) => s"https://nrs.harvard.edu/urn-3:FHCL:$id?buttons=Y"
  val buildRedirectJson = (id: String) => s"https://ids.lib.harvard.edu/ids/iiif/$id/info.json"
  val buildDefaultJpgPage = (id: String, maxWidth: Int) => s"https://ids.lib.harvard.edu/ids/iiif/$id/full/$maxWidth,/0/default.jpg"

  implicit val formats = DefaultFormats

  def main(args: Array[String]): Unit = {

    // 获取页面的Json，一条Json对应多个结果集（图片+内容）
    //      val jsonFile =
    //        {
    //          for (i <- 16 until 17 ) yield {
    //            val offset = i * 50 // 0 ~ 5900
    //            val get = new HttpGet(buildOriginalJsonPage(offset))
    //            get.setHeader("Host", "images.hollis.harvard.edu")
    //            get.setHeader("Connection", "keep-alive")
    //            get.setHeader("Cache-Control", "max-age=0")
    //            get.setHeader("Upgrade-Insecure-Requests", "1")
    //            get.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
    //            get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    //            get.setHeader("Accept-Encoding", "gzip, deflate, br")
    //            get.setHeader("Accept-Language", "ja,en-US;q=0.9,en;q=0.8,zh-CN;q=0.7,zh;q=0.6")
    //            get.setHeader("Authorization", "Bearer eyJraWQiOiJwcmltb0V4cGxvcmVQcml2YXRlS2V5LTAxSFZEIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJQcmltbyIsImp0aSI6IiIsImV4cCI6MTU0NDEwNjA4NywiaWF0IjoxNTQ0MDE5Njg3LCJ1c2VyIjoiYW5vbnltb3VzLTEyMDVfMTQyMTI3IiwidXNlck5hbWUiOm51bGwsInVzZXJHcm91cCI6IkdVRVNUIiwiYm9yR3JvdXBJZCI6bnVsbCwidWJpZCI6bnVsbCwiaW5zdGl0dXRpb24iOiIwMUhWRCIsInZpZXdJbnN0aXR1dGlvbkNvZGUiOiIwMUhWRCIsImlwIjoiMTgzLjEzNC41Mi40NyIsInBkc1JlbW90ZUluc3QiOm51bGwsIm9uQ2FtcHVzIjoiZmFsc2UiLCJsYW5ndWFnZSI6ImVuX1VTIiwiYXV0aGVudGljYXRpb25Qcm9maWxlIjoiIiwidmlld0lkIjoiSFZEX0lNQUdFUyIsImlsc0FwaUlkIjpudWxsLCJzYW1sU2Vzc2lvbkluZGV4IjoiIn0.qLQP-H8OLtR3JE7lUEblPac4sH1p8crbEXLuJm9IS7j2AYxMaQo3Dq9yeWwZSwwjZlJ78pc2joRhIZfz0i4HSw")
    //            get.setHeader("Cookie", "JSESSIONID=F222C681CF1475DCAA1FFCC94EF121E7; _ga=GA1.2.1225141926.1543930452; _gid=GA1.2.406731059.1543930452; sto-id-%3FDir-A_prod%3F01HVD.primo.for.alma.prod.1701-sg=LHFIBMAK; JSESSIONID=1180D7D3177E2028A9CA686A5E472077")
    //
    //            val response = client.execute(get)
    //
    //            val result = parseContent(response.getEntity.getContent)
    //              println(result)
    //            println(i)
    //
    //            if(null != response)
    //              response.close()
    //            if(null != get)
    //              get.completed()
    //
    //            i + "\t" + result
    //          }
    //        } mkString ""
    //        Util.writeFile(pathPageFile, jsonFile)

    // 解析JSON子串，得出每个项的单一结果集
//    val lines =
//      Source.fromFile(new File(pathPageFile), "utf-8")
//        .getLines
//        .toSeq

//    val result =
//      lines
////        .take(4)
//        .map { o =>
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
//        .zipWithIndex
//        .map(o => (o._2 + 1) + "\t" + o._1)
//        //            .foreach(o => println(o))
//        .mkString("\n")
//        Util.writeFile(pathFile, result)
            

    // 从单项结果集文件执行任务
    val lines =
      Source.fromFile(new File(pathFile), "utf-8")
        .getLines
        .toSeq
        println(lines.size) // 799

    // 可直接下载图的单项 => work
    val linesWork = lines
      .filter(_.split("\t")(2).equals("work"))
//          .filter(_.contains("85432"))
//          .foreach(println)
        println(linesWork.size) // 660

    try { //  /*lines.size*/
      for (i <- 100 until 120/*linesWork.size*/ ) {
        threadPool.execute(new DownloadImageProcess(i, linesWork(i)))
      }
    } finally
      threadPool.shutdown()

    // 数据库
    //    val images =
    //      Util.listFiles("/Users/sasaki/git/doc/kj/harvard-yenching/default")
    ////        .take(10)
    //        .filter(_.getName.contains(".jpg"))
    //        .foreach { o =>
    //          val name = o.getName
    //          val array = name.split("-")
    //          val id = array(1) toInt
    //          val page = array(0) toInt
    //          val work_id = array(2) toInt
    //          val source_id = array(3).substring(0, array(3).lastIndexOf(".")) toInt
    //          val image_name = name
    //          val line = lines
    //            .filter(o => o.split("\t")(1).equals("work") && o.contains(s"work$work_id"))(0)
    //            .split("\t")
    //          val json = line(2)
    //          val jsonO = parse(json)
    //          val xml_ = (jsonO \ "pnx" \ "addata" \ "mis1")(0)
    //            .extract[String]
    //            .trim
    //            .replace("\\\"", "\"")
    //            .replace("&", ",") // & 字符引起xml转换异常
    //          val xml =  XML.loadString(xml_)
    //          val title = xml.\("title").\("textElement").text
    //          val author_or_creator =
    //            (xml \ "creator" \ "nameElement").text + ", " +
    //            (xml \ "creator" \ "dates").text + ", " +
    //            (xml \ "creator" \ "namedates").text
    //          val description = xml \ "description" text
    //          val dimensions = xml \ "dimensions" text
    //          val notes = xml \ "notes" text
    //          val creation_date = xml \ "freeDate" text
    //          val repository =
    //            (xml \ "repository" \ "repositoryName").text + " " +
    //            (xml \ "repository" \ "number").take(0).text + " " +
    //            (xml \ "repository" \ "number").take(1).text
    //          val permalink =  s"http://id.lib.harvard.edu/images/olvwork$work_id/catalog"
    //          val y = new Yenching
    //
    //          y.id = id
    //          y.setPage(page)
    //          y.setWork_id(work_id)
    //          y.setSource_id(source_id)
    //          y.setImage_name(name)
    //          y.setTitle(title)
    //          y.setAuthor_or_creator(author_or_creator)
    //          y.setDescription(description)
    //          y.setDimensions(dimensions)
    //          y.setNotes(notes)
    //          y.setCreation_date(creation_date)
    //          y.setRepository(repository)
    //          y.setPermalink(permalink)
    //
    ////          println(y.repository + " | " + y.title + " | " + y.author_or_creator)
    //          saveYenching(y)
    //
    //        }

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
    get.setHeader("Authorization", "Bearer eyJraWQiOiJwcmltb0V4cGxvcmVQcml2YXRlS2V5LTAxSFZEIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJQcmltbyIsImp0aSI6IiIsImV4cCI6MTU0NTQ4NzYyMSwiaWF0IjoxNTQ1NDAxMjIxLCJ1c2VyIjoiYW5vbnltb3VzLTEyMjFfMTQwNzAxIiwidXNlck5hbWUiOm51bGwsInVzZXJHcm91cCI6IkdVRVNUIiwiYm9yR3JvdXBJZCI6bnVsbCwidWJpZCI6bnVsbCwiaW5zdGl0dXRpb24iOiIwMUhWRCIsInZpZXdJbnN0aXR1dGlvbkNvZGUiOiIwMUhWRCIsImlwIjoiMjE4Ljk4LjI2LjUzIiwicGRzUmVtb3RlSW5zdCI6bnVsbCwib25DYW1wdXMiOiJmYWxzZSIsImxhbmd1YWdlIjoiZW5fVVMiLCJhdXRoZW50aWNhdGlvblByb2ZpbGUiOiIiLCJ2aWV3SWQiOiJIVkRfSU1BR0VTIiwiaWxzQXBpSWQiOm51bGwsInNhbWxTZXNzaW9uSW5kZXgiOiIifQ.f13oiNnSVBArl_0t2KeendcqeRx6DiYmHkjFFIYXuJu2haeEXUA3IqP0dTPRLWbMWPOJcpzLZvRGnCnkPxkSwg")
    get.setHeader("Cookie", "JSESSIONID=FEFA01618C587B24AB7E1ACF76AA4CD1; _ga=GA1.2.1225141926.1543930452; sto-id-%3FDir-A_prod%3F01HVD.primo.for.alma.prod.1701-sg=LHFIBMAK")
    //    get.setHeader("If-None-Match", """W/"4636-1520771879000"""")
    //    get.setHeader("If-Modified-Since", "Sun, 11 Mar 2018 12:37:59 GMT")

    val response = client.execute(get)
    //    response.setHeader("ETag", "W/\"4636-1520771879000\"")
    //    response.setHeader("Server", "Apache-Coyote/1.1")
    //    response.setHeader("Date", "Tue, 11 Sep 2018 15:06:03 GMT")

    val result = parseContent(response.getEntity.getContent)
    //    println(result)
    val strContain_manifestUri_ =
      Try {
        result
          .split("\n")
          .filter(_ contains "manifestUri")(0)
      }

    val strContain_manifestUri =
      strContain_manifestUri_ match {
        case Success(o) => strContain_manifestUri_.get
        case Failure(o) => __
      }

    if (response != null)
      response close

    if (__ != strContain_manifestUri)
      (parse(strContain_manifestUri) \ "manifestUri").extract[String]
    else
      __
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
    val builder = StringBuilder.newBuilder
    Source.fromInputStream(input, "UTF-8").getLines().foreach(__ => builder.append(__).append("\n"))
    builder toString
  }

}

class DownloadImageProcess(index: Int, line: String) extends Runnable {

  import WebDiggYenChing._

  override def run(): Unit = {

    implicit val formats = DefaultFormats

    val __ = "_"
    val array = line.split("\t")
    val index_ = array(0).toInt
    val page = array(1).toInt + 1
    val json = array(3)

    val jsonObject = parse(json)
    val id_ = (jsonObject \ "@id").extract[String]
    
    println(s">>page: $page, index: $index_\n>>line: $line")

    val workId = id_.substring(id_.lastIndexOf("work") + 4)
    println(s"workId: $workId")

    // thumbnail page
//    val thumbnail_ = (jsonObject \ "pnx" \ "links" \ "thumbnail")(0).extract[String]
//    val thumbnail = thumbnail_.substring(3)
//    println(s"thumbnail page: $thumbnail")

    // content page
    //    val fhclId = "8151"// thumbnail.substring(thumbnail.indexOf("FHCL:") + 5, thumbnail.indexOf("?"))

    // <work> <image xmlns:xlink=\"http://www.w3.org/TR/xlink\" altComponentID=\"4176736\" componentID=\"W89712_1\" restrictedImage=\"false\" xlink:href=\"http://nrs.harvard.edu/urn-3:FHCL:8151\"> <thumbnail xlink:href=\"http://nrs.harvard.edu/urn-3:FHCL:196422\"/> </image> <title> <textElement>Daughter of Lu, a colleague of Morrison's from Hartung's Photo Shop</textElement> </title> <workType>photographs</workType> <creator> <nameElement>Morrison, Hedda</nameElement> <dates>1908-1991, German</dates> <role>photographer</role> <namedates>Morrison, Hedda 1908-1991, German</namedates> </creator> <structuredDate> <beginDate>1933</beginDate> <endDate>1946</endDate> </structuredDate> <freeDate>ca. 1933-1946</freeDate> <dimensions>16.5 x 10 cm.</dimensions> <topic> <term>girls</term> </topic> <topic> <term>portraits</term> </topic> <culture> <term>Chinese</term> </culture> <materials>gelatin silver process</materials> <location> <type>site</type> <place>Beijing, Beijing Municipality, China</place> </location> <useRestrictions>Harvard-Yenching Library: Access to original photographs, negatives, and albums in the Hedda Morrison photograph collection is restricted. Photographs in the Hedda Morrison photograph collection: Copyright various dates, President and Fellows of Harvard College; all rights reserved. Digital images made from photographs in the Hedda Morrison photograph collection: Copyright 2000, President and Fellows of Harvard College; all rights reserved. Photographs and images from the collection may be reproduced only with written permission. Contact the Harvard-Yenching Library for permissions and fees.</useRestrictions> <repository> <repositoryName>Harvard-Yenching Library</repositoryName> <number>HM06.4783</number> </repository> </work>
    val xml = (jsonObject \ "pnx" \ "addata" \ "mis1")(0)
      .extract[String]
      .trim
      .replace("\\\"", "\"")
      .replace("&", ",") // & 字符引起xml转换异常
      .replace("xlink:href", "xlink-href")
    //      println(xml)
    val fhclIdStr = (XML.loadString(xml) \ "image" \ "@xlink-href").toString

    println("fhclIdStr: " + fhclIdStr)
    val fhclId =
      if ("" != fhclIdStr)
        fhclIdStr.substring(fhclIdStr.lastIndexOf("FHCL:") + 5, fhclIdStr.length)
      else {
        __
        //        thumbnail.substring(thumbnail.indexOf("FHCL:") + 5, thumbnail.indexOf("?"))
      }
    println(s"fhclId: $fhclId")

    if(__ != fhclId) {
      // target page
      // https://nrs.harvard.edu/urn-3:FHCL:5011700?buttons=Y
      val targetPage = buildTargetPage(fhclId)
      // val targetPage = "https://nrs.harvard.edu/urn-3:FHCL:5011700?buttons=Y"
      println(s"target page: $targetPage")

      val redirectPage = parseFromTargetPage2RedirectPage(targetPage)
      println(s"redirect page: $redirectPage")

      if (__ != redirectPage) {
        val redirectId = redirectPage.substring(redirectPage.indexOf("ids:") + 4, redirectPage.length())
        println(s"redirect id: $redirectId")

        val urlInfoJson = buildRedirectJson(redirectId)
        val maxWidth = parseMaxWidthFromInfoJson(urlInfoJson)
        val urlDefaultPage = buildDefaultJpgPage(redirectId, maxWidth)
        //      val imageName = s"$pathThumbnail/$page-${index_ + 1}-$workId-$redirectId-$fhclId.jpg"
        val imageName = s"$pathSh/$page-$index_-$workId-$redirectId-$fhclId.jpg"
        val file = new File(imageName)
        println(s"default page: $urlDefaultPage")

        //    HttpDownload.download(thumbnail, imageName)
        if (!file.exists) {
          println(s"imageName: $imageName")
          HttpDownload.download(urlDefaultPage, imageName)
          println(s">> ========================== $index_ DOWN! =================================")
        }
      }
    }
      
  }
}