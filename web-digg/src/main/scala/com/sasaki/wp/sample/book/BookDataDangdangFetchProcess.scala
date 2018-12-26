package com.sasaki.wp.sample.book

import java.net.URL
import java.util.concurrent.Executors

import org.json4s.DefaultFormats
import org.jsoup.Jsoup

import com.sasaki.wp.persistence.QueryHelper
import com.sasaki.wp.persistence.poso.BookDangDangBestseller
import com.sasaki.wp.util.HttpDownload

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Nov 1, 2018 9:45:34 PM
 * @Description
 */
object BookDataDangdangFetchProcess extends QueryHelper {

  implicit val formats = DefaultFormats

  val root = "http://bang.dangdang.com"

  def buildUrl(year: Int, month: Int, page: Int) =
    s"$root/books/bestsellers/01.00.00.00.00.00-month-$year-$month-2-$page"

  def buildUrlPreYear(year: Int, page: Int) =
    s"$root/books/bestsellers/01.00.00.00.00.00-year-$year-0-2-$page"
    
  val threadPool = Executors.newFixedThreadPool(1)

  def main(args: Array[String]): Unit = {

    var id = 6761
    
    {
      for {
        month <- 1 to 1
        countPage <- 1 to 25
      } yield (month, countPage)
    }
      .foreach {
        case (month, countPage) =>
          val year = 2014
//          val url = buildUrl(year, month, countPage)
          val url = buildUrlPreYear(year, countPage)
          println(s"requested page >>>>>>>>>>>>>>> $countPage, $url" )
          val page = Jsoup.parse(new URL(url), 5000)
          val bang_list_li = page
            .getElementsByClass("bang_list")
            .get(0)
            .getElementsByTag("li")
          for (i <- 0 until bang_list_li.size()) {
            println(s"month: $month, page: $countPage, id: $id")
            val li = bang_list_li.get(i)
            val name = li
              .getElementsByClass("name")
              .get(0)
            val title = name.text()
            val url_item = name
              .getElementsByTag("a")
              .get(0)
              .attr("href")
            val img = li
              .getElementsByClass("pic")
              .get(0)
              .getElementsByTag("img")
              .get(0)
              .attr("src")
            val imageName = s"dangdang_bestseller-$id.jpg"
            HttpDownload.download(img, "/Users/sasaki/bigbook/dangdang_bestseller/2014/" + imageName)
            val div_star = li.getElementsByClass("star").get(0)
            val star_ =
              div_star
                .getElementsByClass("level")
                .get(0)
                .child(0)
                .attr("style")
            val star =
              star_.substring(star_.lastIndexOf(" "), star_.lastIndexOf(";"))
            val num_comment =
              div_star
                .getElementsByTag("a")
                .get(0)
                .text()
                .replace("条评论", "")
                .toInt
            val num_recommend =
              div_star
                .getElementsByClass("tuijian")
                .get(0)
                .text()
                .replace("推荐", "")
            val array = li.getElementsByClass("publisher_info")
              .text()
              .split("/")
            val author_andor_translator = array(0).trim()
            val publishing_date = array(1).trim
            val publisher = {
              if(array.size == 3)
                array(2).trim 
              else
                ""
            }
            //              println(author_andor_translator + " " + publishing_date + " " + publisher)
            val div_price_p = li
              .getElementsByClass("price")
              .get(0)
              .getElementsByTag("p")
              .get(0)
            val price_current =
              div_price_p
                .getElementsByClass("price_n")
                .get(0)
                .text()
                .replace("¥", "")
                .toDouble
            val price_original =
              div_price_p
                .getElementsByClass("price_r")
                .get(0)
                .text()
                .replace("¥", "")
                .replace(",", "")
                .toDouble
            val discount =
              div_price_p
                .getElementsByClass("price_s")
                .get(0)
                .text()
                .replace("折", "")
                .toDouble
            //               println(price_current + " " + price_original + " " + discount)

            val book = new BookDangDangBestseller
            book.id = id
            book.setTitle(title)
            book.setYear(year + "")
            book.setMonth(null)
            book.setAuthor_andor_translator(author_andor_translator)
            book.setUrl_item(url_item)
            book.setPublishing_date(publishing_date)
            book.setNum_star(star)
            book.setNum_comment(num_comment)
            book.setNum_recommend(num_recommend)
            book.setPrice_original(price_original)
            book.setPrice_current(price_current)
            book.setDiscount(discount)
            book.setImage(imageName)
            book.setPublisher(publisher)

            saveBookDangDangBestseller(book)
            id = id + 1
          }
      }
  }
}