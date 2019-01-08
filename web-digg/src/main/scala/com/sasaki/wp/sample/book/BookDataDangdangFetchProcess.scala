package com.sasaki.wp.sample.book

import java.util.concurrent.Executors

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.json4s.DefaultFormats

import com.sasaki.packages.independent
import com.sasaki.wp.persistence.QueryHelper
import com.sasaki.wp.persistence.poso.BookBestseller

 import org.json4s.DefaultFormats
        import org.json4s.JsonMethods
        import org.json4s.jackson.JsonMethods
        import org.json4s.jvalue2extractable
        import org.json4s.jvalue2monadic
        import org.json4s.string2JsonInput
        import com.sasaki.wp.persistence.QueryHelper
        import org.apache.http.impl.client.CloseableHttpClient
        import org.jsoup.Jsoup
        import com.sasaki.wp.util.HttpDownload
        import java.net.URL
        
/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Nov 1, 2018 9:45:34 PM
 * @Description
 */
object BookDataDangdangFetchProcess extends QueryHelper {

  implicit val formats = DefaultFormats

  /**
   * dangdang
   */
//    val root = "http://bang.dangdang.com"
//    def buildUrl(year: Int, month: Int, page: Int) =
////      s"$root/books/bestsellers/01.00.00.00.00.00-month-$year-$month-2-$page"
//      s"http://bang.dangdang.com/books/bestsellers/01.00.00.00.00.00-recent30-0-0-2-$page"
//    def buildUrlPreYear(year: Int, page: Int) =
//      s"$root/books/bestsellers/01.00.00.00.00.00-year-$year-0-2-$page"

  /**
   * jingdong
   */
  val root = "https://book.jd.com"
  // 2018年历月
  def buildUrl(month: Int, page: Int) =
    s"https://book.jd.com/booktop/0-0-0.html?category=1713-0-0-0-$month-$page#comfort"
  // 历年
  def jd_2018(page: Int) = s"https://book.jd.com/booktop/0-0-0.html?category=1713-0-0-0-10001-$page#comfort"
  def jd_2017(page: Int) = s"https://book.jd.com/booktop/0-0-0.html?category=1713-0-0-0-10005-$page#comfort"

  val threadPool = Executors.newFixedThreadPool(1)

  def main(args: Array[String]): Unit = {

    /**
     * list from dangdang
     */
//        var id = 9661
        
//        {
//          for {
//            month <- 12 to 12
//            countPage <- 7 to 25
//          } yield (month, countPage)
//        }
//          .foreach {
//            case (month, countPage) =>
//              val year = 2018
//              val url = buildUrl(year, month, countPage)
////              val url = buildUrlPreYear(year, countPage)
//              println(s"requested page >>>>>>>>>>>>>>> $countPage, $url" )
//              val page = Jsoup.parse(new URL(url), 5000)
//              val bang_list_li = page
//                .getElementsByClass("bang_list")
//                .get(0)
//                .getElementsByTag("li")
//              for (i <- 0 until bang_list_li.size()) {
//                println(s"month: $month, page: $countPage, id: $id")
//                val li = bang_list_li.get(i)
//                val name = li
//                  .getElementsByClass("name")
//                  .get(0)
//                val title = name.text()
//                val url_item = name
//                  .getElementsByTag("a")
//                  .get(0)
//                  .attr("href")
//                val img = li
//                  .getElementsByClass("pic")
//                  .get(0)
//                  .getElementsByTag("img")
//                  .get(0)
//                  .attr("src")
//                val imageName = s"dangdang_bestseller-$id.jpg"
//                HttpDownload.download(img, "/Users/sasaki/bigbook/dangdang_bestseller/dangdang/2018_annual/" + imageName)
//                val div_star = li.getElementsByClass("star").get(0)
//                val star_ =
//                  div_star
//                    .getElementsByClass("level")
//                    .get(0)
//                    .child(0)
//                    .attr("style")
//                val star =
//                  star_.substring(star_.lastIndexOf(" "), star_.lastIndexOf(";"))
//                val num_comment =
//                  div_star
//                    .getElementsByTag("a")
//                    .get(0)
//                    .text()
//                    .replace("条评论", "")
//                    .toInt
//                val num_recommend =
//                  div_star
//                    .getElementsByClass("tuijian")
//                    .get(0)
//                    .text()
//                    .replace("推荐", "")
//                val array = li.getElementsByClass("publisher_info")
//                  .text()
//                  .split("/")
//                val author_andor_translator = array(0).trim()
//                val publishing_date = array(1).trim
//                val publisher = {
//                  if(array.size == 3)
//                    array(2).trim
//                  else
//                    ""
//                }
//                //              println(author_andor_translator + " " + publishing_date + " " + publisher)
//                val div_price_p = li
//                  .getElementsByClass("price")
//                  .get(0)
//                  .getElementsByTag("p")
//                  .get(0)
//                val price_current =
//                  div_price_p
//                    .getElementsByClass("price_n")
//                    .get(0)
//                    .text()
//                    .replace("¥", "")
//                    .replace("&en;", "")
//                    .toDouble
//                val price_original =
//                  div_price_p
//                    .getElementsByClass("price_r")
//                    .get(0)
//                    .text()
//                    .replace("¥", "")
//                    .replace(",", "")
//                    .toDouble
//                val discount =
//                  div_price_p
//                    .getElementsByClass("price_s")
//                    .get(0)
//                    .text()
//                    .replace("折", "")
//                    .toDouble
//                //               println(price_current + " " + price_original + " " + discount)
//    
//                val book = new BookBestseller
//                book.id = id
//                book.setTitle(title)
//                book.setYear(year + "")
//                book.setMonth("12")
//                book.setAuthor_andor_translator(author_andor_translator)
//                book.setUrl_item(url_item)
//                book.setPublishing_date(publishing_date)
//                book.setNum_star(star)
//                book.setNum_comment(num_comment)
//                book.setNum_recommend(num_recommend)
//                book.setPrice_original(price_original)
//                book.setPrice_current(price_current)
//                book.setDiscount(discount)
//                book.setImage(imageName)
//                book.setPublisher(publisher)
//                book.setSource("")
//    
//                saveBookDangDangBestseller(book)
//                id = id + 1
//              }
//          }

    /**
     * dang dang detail isbn & category
     */
//        val list = listBookInfo.filter(_._1 == 9627)
//        println(list.size)
//        for (i <- 0 until list.size) {
//          val id = list(i)._1
//          val url_item = list(i)._2
//    
//          try {
//            val page = Jsoup.parse(new URL(url_item), 15000)
//    
//            val li_isbn =
//              page
//                .getElementsContainingOwnText("国际标准书号ISBN")
//                .get(0)
//                .text()
//            val isbn = li_isbn.substring(li_isbn.lastIndexOf("：") + 1)
//            val list_span_lie =
//              page.getElementById("detail-category-path")
//                .getElementsByClass("lie")
//    
//            val category = {
//              for (i <- 0 until list_span_lie.size()) yield {
//                list_span_lie
//                  .get(i)
//                  .text
//                  .split(">")
//                  .map(_ trim)
//                  .mkString("|")
//              }
//            }.mkString(";")
//    
//            val book = new BookBestseller
//            book.id = id
//            book.setCategory(category)
//            book.setIsbn(isbn)
//    
//            println(id + " " + url_item + " " + category)
//            updateBookInfo(book)
//          } catch {
//            case t: Throwable =>
//              println("error: "+ url_item)
//              t.printStackTrace()
//          }
//        }

    /**
     * list from jd
     */

//        var id = 9861
//        
//        {
//          for {
//            month <- 12 to 12
//            countPage <- 1 to 5
//          } yield (month, countPage)
//        }.foreach {
//      case (month, countPage) =>
//        val year = 2017
////        val url = jd_2017(countPage)
//        val url = s"http://book.jd.com/booktop/0-0-0.html?category=1713-0-0-0-10003-$countPage#comfort"
//        //            val url = buildUrl(month, countPage)
//        println(s"requested page >>>>>>>>>>>>>>> $month, $countPage, $url")
//        val page = Jsoup.parse(new URL(url), 15000)
//        val list = page.getElementsByClass("clearfix").get(0).getElementsByTag("li")
//
//        for (i <- 0 until list.size()) {
//          val li = list.get(i)
//          val div_detail = li.getElementsByClass("p-detail").get(0)
//          val title = div_detail.getElementsByTag("a").get(0).text()
//          val url_item_ = div_detail.getElementsByTag("a").get(0).attr("href")
//          val url_item = "https:/" + url_item_.substring(1)
//          val list_dl = div_detail.getElementsByTag("dl")
//          val author_andor_translator = list_dl.get(0).getElementsByTag("dd").text()
//          val publisher = list_dl.get(1).getElementsByTag("dd").text()
//
//          val page_item = Jsoup.parse(new URL(url_item), 15000)
//          val isbn_ = page_item.getElementsContainingOwnText("ISBN").text()
//          val isbn = isbn_.substring(isbn_.lastIndexOf("：") + 1)
//          val publishing_date_ = page_item.getElementsContainingOwnText("出版时间").get(0).text()
//          val publishing_date = publishing_date_.substring(publishing_date_.lastIndexOf("：") + 1)
//          val img = "https:" + page_item
//            .getElementById("spec-n1")
//            .getElementsByTag("img")
//            .get(0)
//            .attr("src")
//          val imageName = s"jingdong_bestseller-$id.jpg"
//          HttpDownload.download(img, s"/Users/sasaki/bigbook/bestseller/jingdong/2018/12/" + imageName)
//          val category = page_item
//            .getElementsByClass("crumb")
//            .get(0)
//            .text()
//            .split(">")
//            .map(_ trim)
//            .mkString("|")
//
//          val book = new BookBestseller
//          book.id = id
//          book.setIsbn(isbn)
//          book.setTitle(title)
//          book.setYear(year + "")
//          book.setMonth(month + "")
//          book.setAuthor_andor_translator(author_andor_translator)
//          book.setUrl_item(url_item)
//          book.setPublishing_date(publishing_date)
//          book.setCategory(category)
//          book.setImage(imageName)
//          book.setPublisher(publisher)
//          book.setSource("jd")
//
//          saveBookDangDangBestseller(book)
//          id = id + 1
//        }
//    }

    /**
     * price from jd
     */
    
    val threadPool = Executors.newFixedThreadPool(20)
    
    val items = listBookJDItem.filter(_._1 >= 9661)
    
    try {
      for (i <- 0 until items.size) {
        val id = items(i)._1
        val url_item = items(i)._2
        threadPool.execute(new JDPriceProcess(id, url_item))
      }
    } finally {
      threadPool.shutdown()
    }
  }
}

class JDPriceProcess(id: Long, url_item: String) extends Runnable with QueryHelper {
  
  implicit val formats = DefaultFormats

  def run(): Unit = {
    
    val client = HttpClientBuilder.create().build()
    val id_item = url_item.substring(url_item.lastIndexOf("/") + 1, url_item.lastIndexOf("."))
    val url_request = s"https://p.3.cn/prices/mgets?type=1&skuIds=J_$id_item&callback=jQuery9492951&_=1545964109414"
    println(id + " " + url_request)
    val get = new HttpGet(url_request)
    val response = client.execute(get).getEntity.getContent
    val data = independent.parseRequestContent(response)
    val data_json = data.substring(data.lastIndexOf("{"), data.lastIndexOf("}") + 1)
    val jsonObj = JsonMethods.parse(data_json)
    import org.json4s.JsonAST._
    val price_original = (jsonObj \ "m").extract[String].toDouble
    val price_current = (jsonObj \ "op").extract[String].toDouble
    val discount = price_current / price_original * 10
    val book = new BookBestseller
    book.id = id
    book.setPrice_original(price_original)
    book.setPrice_current(price_current)
    book.setDiscount(discount)

    updateBookInfoJD(book)
    println(s"DONE! >>>>>> $id")
    
    client.close()
  }

}