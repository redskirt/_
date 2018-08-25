package com.sasaki.wp.sample

import org.jsoup.Jsoup
import org.apache.http.client.methods.HttpGet
import scala.util.control.Breaks
import java.net.URL

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Aug 25, 2018 8:43:04 PM
 * @Description
 */
object WebDiggBristol {
  
  val client = org.apache.http.impl.client.HttpClients.createDefault()
  val root = "https://www.hpcbristol.net"

  def main(args: Array[String]): Unit = {

    val ul = Jsoup.parse(new java.io.File("/Users/sasaki/Desktop/bristol.html"), "utf-8")
      .getElementsByClass("collection-tree")
      .first()
      .children()

    {
      for (i <- 0 until ul.size) yield {
        ul.get(i).child(0).getElementsByTag("a").attr("href")
      }
    }
    .filter(_ != "")
    .foreach { o =>
      val url = s"$root$o"
      println(url)
      val get = new HttpGet(url)
      val response = client.execute(get)
      val ul = Jsoup.parse(new URL(url), 5000).getElementsByTag("ul").first()
      
      Breaks.breakable {
        for (i <- 0 until 1000) {
          if (ul.children().isEmpty()) {
            Breaks.break()
          } else {
            println(ul)
          }
        }
      }
    }
  }

}