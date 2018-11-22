package com.sasaki.wp.sample.book

import org.json4s.DefaultFormats
import com.sasaki.wp.persistence.QueryHelper
import org.jsoup.Jsoup
import java.net.URL
import com.sasaki.wp.persistence.poso.Book
import java.util.concurrent.Executors
import org.jsoup.nodes.Document

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Nov 1, 2018 9:45:34 PM
 * @Description 
 */
class BookDataFetchProcess(
    i: Int, 
    pageNum: Int,
    _status: String,
    _order: String,
    page: Document
    ) extends QueryHelper with Runnable {

  import BookDataFetchProcess._

  def run(): Unit = {
    val id = (pageNum - 1) * 50 + i + 1 + 315000 /*追加*/
    println(id)
    
    val listBox = page.getElementById("listBox")
    val items = listBox.getElementsByClass("item")

    val item = items.get(i)
    val shopid = item.attr("shopid")
    val itemid = item.attr("itemid")
    val userid = item.attr("userid")
    val price = item.attr("price").toDouble.toInt
    val title = item
      .getElementsByClass("title")
      .get(0)
      .getElementsByTag("a")
      .get(0)
      .text()
    val normal_title = item.getElementsByClass("normal-title")
    val normal_text = item.getElementsByClass("normal-text")
    val title_text =
      { // Vector((作者 :,网格本3本合拍), (出版社 :,网格本3本合拍), (出版时间 :,1984), (装帧 :,平装))
        for (j <- 0 until normal_title.size())
          yield (normal_title.get(j).text(), normal_text.get(j).text())
      }
    val __ = ("", "")
    val author = title_text.find(_._1 .contains("作者")).getOrElse(__)._2
    val publisher = title_text.find(_._1.contains("出版社")).getOrElse(__)._2
    val publish_date = title_text.find(_._1.contains("出版时间")).getOrElse(__)._2
    val print_date = title_text.find(_._1.contains("印刷时间")).getOrElse(__)._2
    val cover = title_text.find(_._1.contains("装帧")).getOrElse(__)._2
    val size = "32"
    val edition = title_text.find(_._1.contains("版次")).getOrElse(__)._2
    val print_times = title_text.find(_._1.contains("印次")).getOrElse(__)._2
    val quality = item.getElementsByClass("quality").get(0).text()
    val putaway_date = item.getElementsByClass({
      if("0".equals(_status))
        "add-time-box"
      else
        "ship-fee-box"
    }).get(0).text()
    val array = putaway_date.split(' ')
    val putaway_or_deal_date = array(0)
    val deal_type = array(1)
    val data_source = "kfz"
    val bookstore = item.getElementsByClass("user-info-link").get(0).text()
    val url = s"$root_book/$shopid/$itemid/"
    val url_store = s"$root_shop/$shopid"
    val url_storekeeper = s"$root_shop/$userid"
    val status = "normal"
    val `type` = "old"

    val book = new Book
    book.id = id
    book.setId_item(itemid)
    book.setId_storekeeper(userid)
    book.setId_store(shopid)
    book.setTitle(title)
    book.setAuthor(author)
    book.setPublisher(publisher)
    book.setPublish_date(publish_date)
    book.setPrint_date(print_date)
    book.setCover(cover)
    book.setSize(size)
    book.setEdition(edition)
    book.setPrint_times(print_times)
    book.setQuality(quality)
    book.setPrice(price)
    book.setPutaway_or_deal_date(putaway_or_deal_date)
    book.setDeal_type(deal_type)
    book.setData_source(data_source)
    book.setBookstore(bookstore)
    book.setUrl(url)
    book.setUrl_store(url_store)
    book.setUrl_storekeeper(url_storekeeper)
    book.setStatus(status)
    book.setType(`type`)
    book.setOrderby({
      if ("".equals(_order))
        "default"
      else
        "putaway"
    })
    
    book.setBatch(16)

    saveBook(book)
  }
}

object BookDataFetchProcess {

  import com.sasaki.wp.enums.E._

  implicit val formats = DefaultFormats

  val root = "http://search.kongfz.com"
  val root_book = "http://book.kongfz.com"
  val root_shop = "http://shop.kongfz.com"
  
  def buildUrl(page: Int, status: String/* 0 在售 1 已售 */, order: String/* &order=6 */) = 
    s"$root/product_result/?key=%E7%BD%91%E6%A0%BC%E6%9C%AC&itemfilter=0&status=$status$order&pagenum=$page"
  
  val threadPool = Executors.newFixedThreadPool(25)

  def main(args: Array[String]): Unit = {

//    val page_1 = Jsoup.parse(new URL(buildUrl_sale_putaway(1)), 5000)
    //    val count = page_1
    //      .getElementById("crumbsBar")
    //      .getElementsByClass("crumbs-nav-start")
    //      .get(0)
    //      .getElementsByTag("span")
    //      .get(0)
    //      .text()
    //      .toInt
//    val listBox = page_1.getElementById("listBox")
//    val items = listBox.getElementsByClass("item")

    try { //  /*lines.size*/
      for (pageNum <- 1 to 100) {
        println("page: " + pageNum)
        
        /**
         * 参数配对 
         * status	order 
         * "0"			""
         * "0"			"&order=6"
         * "1"			""
         * "1"			"&order=6"
         * 
         * NOTE!
         * 执行三步曲：
         * 1. 改运行参数
         * 2. 改batch数
         * 3. 改ID累加器
         */
        val url = buildUrl(pageNum, "1", "&order=6")
        println(url)
        val page = Jsoup.parse(new URL(url), 5000)
        
        for (i <- 0 until 50/*items.size()*/) {
          threadPool.execute(new BookDataFetchProcess(i, pageNum, "1", "&order=6", page))
        }
        
        Thread.sleep(3000)// 防止请求频繁 
      }
    } finally
      threadPool.shutdown()
  }
}