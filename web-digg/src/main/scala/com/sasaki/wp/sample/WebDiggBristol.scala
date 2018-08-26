package com.sasaki.wp.sample

import org.jsoup.Jsoup
import org.apache.http.client.methods.HttpGet
import scala.util.control.Breaks
import java.net.URL
import com.sasaki.wp.util.Util
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import com.sasaki.wp.util.NetStreamIOHandler
import com.sasaki.wp.util.NetStreamIOHandler

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Aug 25, 2018 8:43:04 PM
 * @Description
 */
object WebDiggBristol {
  
  val client = org.apache.http.impl.client.HttpClients.createDefault()
  val root = "https://www.hpcbristol.net"
  val file = "/Users/sasaki/git/_/web-digg/src/main/resources/bristol-image-list.txt"

  def main(args: Array[String]): Unit = {

    //    val ul = Jsoup.parse(new java.io.File("/Users/sasaki/Desktop/bristol.html"), "utf-8")
    //      .getElementsByClass("collection-tree")
    //      .first()
    //      .children()
    //    {
    //      for (i <- 0 until ul.size) yield {
    //        ul.get(i).child(0).getElementsByTag("a").attr("href")
    //      }
    //    }
    //    .filter(_ != "")
    //    .foreach(println)

    /**
     * 抓取照片链接
     */
    //    val array = new ArrayBuffer[String]
    //    Seq(
    //    "/collections/anthony-augustus",
    //    "/collections/armstrong-william",
    //    "/collections/atchison-george",
    //    "/collections/baggs-ernest",
    //    "/collections/banister-family",
    //    "/collections/barnett-marina",
    //    "/collections/bayley-arthur",
    //    "/collections/billie-love-historical",
    //    "/collections/bowra-edward",
    //    "/collections/british-steel-archive-project-bsap",
    //    "/collections/cadbury-research-library",
    //    "/collections/cadbury-research-library-collection",
    //    "/collections/carey-frederic",
    //    "/collections/carrall-family",
    //    "/collections/carstairs-jamie",
    //    "/collections/chatterton-jocelyn",
    //    "/collections/cooper-william",
    //    "/collections/cottrell-family",
    //    "/collections/crellin-thomas",
    //    "/collections/crossley-evans-martin",
    //    "/collections/crowley-john",
    //    "/collections/darwent-charles",
    //    "/collections/davidson-francis",
    //    "/collections/dhoot-helen",
    //    "/collections/drew-edward-bangs",
    //    "/collections/elliott-family",
    //    "/collections/ephgrave-jack",
    //    "/collections/evans-david",
    //    "/collections/fiddament-arthur",
    //    "/collections/fu-bingchang",
    //    "/collections/funnell-martin",
    //    "/collections/hagger-f",
    //    "/collections/hall-ben",
    //    "/collections/hayward-family",
    //    "/collections/hayward-tita-and-gerry",
    //    "/collections/hedgeland-reginald",
    //    "/collections/henderson-david-marr",
    //    "/collections/henriot-christian",
    //    "/collections/hillier",
    //    "/collections/hobbs-family",
    //    "/collections/hookham-felix",
    //    "/collections/hughes-thomas",
    //    "/collections/hulme-oliver",
    //    "/collections/hutchinson-family",
    //    "/collections/images-books",
    //    "/collections/johns-thomas",
    //    "/collections/kelsey-family",
    //    "/collections/kiki-de-chaffoy",
    //    "/collections/klein-peter-0",
    //    "/collections/knight-family",
    //    "/collections/lang-archibald",
    //    "/collections/liu-haiyan",
    //    "/collections/maxwell-family",
    //    "/collections/miscellaneous",
    //    "/collections/mitchell-eleanor",
    //    "/collections/morrison-hedda",
    //    "/collections/national-archives-kew",
    //    "/collections/needham-joseph",
    //    "/collections/northup-lesley",
    //    "/collections/oswald-john",
    //    "/collections/palmer-william",
    //    "/collections/peck-harold",
    //    "/collections/phipps-ann",
    //    "/collections/pickens-jr-rev-claude-l",
    //    "/collections/potts-george",
    //    "/collections/richard-eleanor",
    //    "/collections/riddle-george",
    //    "/collections/rosholt-malcolm",
    //    "/collections/royal-hampshire-regiment",
    //    "/collections/rue-henry",
    //    "/collections/ruxton-family",
    //    "/collections/sangha-ranjit-singh",
    //    "/collections/scherr-jennifer",
    //    "/collections/shaji-massacre-1925",
    //    "/collections/sinton-john",
    //    "/collections/smith-sydney",
    //    "/collections/stanfield-family",
    //    "/collections/steen-james",
    //    "/collections/stevens-dennis",
    //    "/collections/sullivan-john",
    //    "/collections/swire-g-warren",
    //    "/collections/taylor-edgar",
    //    "/collections/thompson-john",
    //    "/collections/tianjin-post-cards",
    //    "/collections/tocher-forbes-scott",
    //    "/collections/trobridge-george",
    //    "/collections/university-of-bristol-library",
    //    "/collections/wheeler-charles",
    //    "/collections/wilkinson-edward",
    //    "/collections/williams-jim",
    //    "/collections/wyatt-smith-stanley",
    //    "/collections/young-stewart"
    //    )
    //    .foreach { o =>
    //      val url = s"$root$o"
    ////    lazy  val get = new HttpGet(url)
    ////    lazy  val response = client.execute(get)
    ////      println(url)
    //      val ul = Jsoup.parse(new URL(url), 5000)
    //        .getElementsByClass("row").first()
    //
    //      Breaks.breakable {
    //          val lis = ul.children()
    //          if (!lis.isEmpty()) {
    //            for (i <- 0 until 1000 /* page */ ) {
    //              val url_with_page = s"$url?page=$i"
    ////              println(url_with_page)
    //              val ul_with_page = Jsoup.parse(new URL(url_with_page), 5000).getElementsByClass("row-lg-4")
    //              if(!ul_with_page.isEmpty()) {
    //                val lis_with_page = ul_with_page.first().children()
    //                if (lis_with_page.isEmpty())
    //                  Breaks.break()
    //                else {
    //                  for (j <- 0 until lis_with_page.size() /* li */ ) {
    //                    val li_with_page = lis_with_page.get(j)
    //                    val href = li_with_page.getElementsByClass("overlay-link").first().attr("href")
    //                    val url_content = s"$root$href"
    //                    println(url_content)
    //                    array += url_content
    //                  }
    //                }
    //              } else Breaks.break()
    //            }
    //          }
    //        }
    //    }
    //    Util.writeFile(file, array.mkString("\n"))

    /**
     * 抓取照片文件及信息
     */
    Source.fromFile(file).getLines()
      .take(1)
      .foreach { o =>
        println(o)
        val document = Jsoup.parse(new URL(o), 5000)
        val title = document.getElementById("page-title").child(0).text()
        val notes = document
          .getElementsByClass("field__note").first()
          .getElementsByClass("field__item").first()
          .getElementsByTag("p").text()
        val collection = {
          val a = document
            .getElementsByClass("field__collection").first()
            .getElementsByClass("field__item").first()
            .getElementsByTag("a")

          a.text() + s"; $root" + a.attr("href")
        }
        val identifier = document
          .getElementsByClass("field__identifier").first()
          .getElementsByClass("field__item").first()
          .text()
        val copyright = {
          val a = document
            .getElementsByClass("field__rights").first()
            .getElementsByClass("field__item").first()
            .getElementsByTag("a")

          a.text() + s"; $root" + a.attr("href")
        }
        val estimated_date = document
          .getElementsByClass("field__date-estimate").first()
          .getElementsByClass("field__item").first()
          .text()
        val tag_item = document
          .getElementsByClass("field__tags").first()
          .getElementsByClass("field__item")
        val tag = {
          for (i <- 0 until tag_item.size()) yield {
            tag_item.get(i).child(0).text()
          }
        } mkString ("|")
        val media = document
          .getElementsByClass("field__image-type").first()
          .getElementsByClass("field__item").first()
          .text()

        val src = root + document
          //          .getElementsByClass("image-wrapper")
          //          .first()
          //          .getElementsByTag("img")
          //          .first()
          //          .attr("src")
          .getElementsByClass("field__download").first()
          .getElementsByTag("li").first()
          .getElementsByTag("a").first()
          .attr("href")

        println(src)
        NetStreamIOHandler(src, "/Users/sasaki/Desktop/a.jpg").download
      }
  }
}