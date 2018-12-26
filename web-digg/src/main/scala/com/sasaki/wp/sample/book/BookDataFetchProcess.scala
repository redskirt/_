package com.sasaki.wp.sample.book

import java.net.URL
import java.util.concurrent.Executors

import org.json4s.DefaultFormats
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.retry.RecoveryCallback
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

import com.sasaki.wp.persistence.QueryHelper
import com.sasaki.wp.persistence.poso.Book
import com.yunpian.sdk.YunpianClient
import com.yunpian.sdk.constant.YunpianConstant

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Nov 1, 2018 9:45:34 PM
 * @Description 
 */
class BookDataFetchProcess(
    i: Int, 
    pageNum: Int,
    param: (String, String, String, String), 
    page: Document,
    count: Long,
    lastBatch: Int
    ) extends QueryHelper with Runnable {

  import BookDataFetchProcess._

  def run(): Unit = {
    val id = (pageNum - 1) * 50 + i + 1 + count /*追加*/
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
    val author = title_text.find(_._1.contains("作者")).getOrElse(__)._2
    val publisher = title_text.find(_._1.contains("出版社")).getOrElse(__)._2
    val publish_date = title_text.find(_._1.contains("出版时间")).getOrElse(__)._2
    val print_date = title_text.find(_._1.contains("印刷时间")).getOrElse(__)._2
    val cover = title_text.find(_._1.contains("装帧")).getOrElse(__)._2
    val size = "32"
    val edition = title_text.find(_._1.contains("版次")).getOrElse(__)._2
    val print_times = title_text.find(_._1.contains("印次")).getOrElse(__)._2
    val quality = item.getElementsByClass("quality").get(0).text()
    val putaway_date = item.getElementsByClass({
      if("0".equals(param._1))
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
    val location = item.getElementsByClass("user-place").text()

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
    book.setKeyword(param._4)
    book.setLocation(location)
    book.setOrderby({
      if ("".equals(param._2))
        "default"
      else
        "putaway"
    })
    
    book.setBatch(lastBatch + 1)

    saveBook(book)
  }
}

object BookDataFetchProcess extends QueryHelper {

  implicit val formats = DefaultFormats

  val root = "http://search.kongfz.com"
  val root_book = "http://book.kongfz.com"
  val root_shop = "http://shop.kongfz.com"
  
  def buildUrl(page: Int,  param: (String, String, String, String)) = 
    s"$root/product_result/?key=${param._3}&itemfilter=0&status=${param._1}${param._2}&pagenum=$page"
  
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

    // 构建重试模板实例
    val retry = new RetryTemplate
    //设置重试策略，主要设置重试次数
    val retryPolicy = new SimpleRetryPolicy
    retryPolicy.setMaxAttempts(10)
    //设置重试回退操作策略，主要设置重试间隔时间
    val fixedBackOffPolicy = new FixedBackOffPolicy
    fixedBackOffPolicy.setBackOffPeriod(3000)
    retry.setRetryPolicy(retryPolicy)
    retry.setBackOffPolicy(fixedBackOffPolicy)

    val lastBatch = queryMaxBatch
    val 网格本 = "%E7%BD%91%E6%A0%BC%E6%9C%AC"
    val 二月河文集 = "%E4%BA%8C%E6%9C%88%E6%B2%B3%E6%96%87%E9%9B%86"
    val params = Seq(
      // status /* 0 在售 1 已售 */  order  keyword
//                Tuple4("0",  "",           网格本,    "网格本"),
                Tuple4("0",  "&order=6",   网格本,    "网格本"),
                Tuple4("1",  "",           网格本,    "网格本"),
                Tuple4("1",  "&order=6",   网格本,    "网格本")//,
//
      // 一周一次
//      Tuple4("0", "&order=6", 二月河文集, "二月河文集"),
//      Tuple4("1", "&order=6", 二月河文集, "二月河文集")
    )
        
    try {
      val client = new YunpianClient("47b97b332b9e3448821d0c8bf226c885").init()
      
      val ENCODING = "UTF-8"
      val param = client.newParam(3)
      param.put(YunpianConstant.MOBILE, "17091920677,18101823205")
     
      params.foreach {
        o =>
          val status = o._1
          val order = o._2
          val count = queryMaxBookId
          val tryPage = Jsoup.parse(new URL(buildUrl(1, o)), 5000)
          val pageCount = tryPage
            .getElementById("pageTop")
            .getElementsByTag("span")
            .get(3)
            .text
            .toInt
          
          println("count " + count)
          for (pageNum <- 1 to pageCount) {
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
            import scala.util.{ Try, Success, Failure }

            val url = buildUrl(pageNum, o)
            println(url)

            // 使用Retry方式请求page
            val recallPage = new RetryCallback[Document, Exception] {
              override def doWithRetry(context: RetryContext): Document = {

                val page_ = Try {
                  Jsoup.parse(new URL(url), 5000)
                }

                val page = page_ match {
                  case Success(o) => page_.get
                  case Failure(o) =>
                    println(">> retring ... ")
                    throw new RuntimeException // 抛出异常触发重试
                }

                page
              }
            }

            // 重试失败后执行
            val recovery = new RecoveryCallback[Document] {
              override def recover(context: RetryContext): Document = {
                // 所有 retry 均失败后，程序中止
                threadPool.shutdown
                null
              }
            }

            // 调用重试机制
            val page = retry.execute(recallPage, recovery)

            // 多线程仅负责提交数据库写入效率
            for (i <- 0 until 50 /*items.size()*/ ) {
              threadPool.execute(new BookDataFetchProcess(i, pageNum, o, page, count, lastBatch))
            }

            Thread.sleep(3000) // 防止请求频繁
          }
      }
      
      param.put(YunpianConstant.TEXT, s"【刘巍的数据平台】${com.sasaki.packages.independent.TODAY}新增数据20000，数据总量${ countBook }。")
      val result = client.sms().batch_send(param)
      println("code: " + result.getCode + "\nmessage: " + result.getMsg + "\ndata: " + result.getData + "\ndetail: " + result.getDetail)
      client.close
    } finally
      threadPool.shutdown()
  }
}