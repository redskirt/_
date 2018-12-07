package com.sasaki.wp.persistence.poso

import java.sql.Timestamp
import org.squeryl.annotations.Column
import scala.beans.BeanProperty
import org.springframework.retry.RecoveryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryCallback
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.support.RetryTemplate
import org.jsoup.Jsoup
import java.net.URL

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 15, 2018 4:32:30 PM
 * @Description
 */

class Bean {
  @Column("id")
  var id: Long = _
}

case class Account(val account: String, val password: String) extends Bean {
  var status: String = _
}

case class Metadata(val account: String, val cookie: String) extends Bean {
  @Column("type")
  var _type: String = _
  var timestamp: Timestamp = new Timestamp(System.currentTimeMillis())

  def setType(_type: String): Metadata = { this._type = _type; this }
}

// --------------------------------------  以上是WebDigg项目的查询接口，以下为VirtualSH  -------------------------------

case class Source(
  @Column("page_id") 
  val pageId:           Long,
  
  val city: String,
  val `type`: String) extends Bean {
  var content: String = _
  @Column("image_id") 
  var imageId: String = _
  @Column("image_name") 
  var imageName: String = _
  var timestamp: Timestamp = new Timestamp(System.currentTimeMillis())
}

class View extends Bean {
      
  @Column("page_id")
  var pageId: Long = _ // 仅VSH来源的照片
  @Column("image_id")
  var imageId: String = _
  @Column("image_name")
  var imageName: String = _
  var city: String = _
  var title: String = _
  var collection: String = _
  var location: String = _
  var extent: String = _
  var year: String = _
  var date: String = _
  var photographer: String = _
  @Column("estimated_date")
  var estimatedDate: String = _
  @Column("image_type")
  var imageType: String = _
  @Column("material_form_of_image")
  var materialFormOfImage: String = _
  @Column("private_repository")
  var privateRepository: String = _
  var notes: String = _
  @Column("keywords_en")
  var keywordsEn: String = _
  @Column("keywords_fr")
  var keywordsFr: String = _
  @Column("street_name")
  var streetName: String = _
  var repository: String = _
  var building: String = _
  @Column("related_image")
  var relatedImage: String = _
  val timestamp: Timestamp = new Timestamp(System.currentTimeMillis())
}

class Bristol extends Bean {
  var original_image_name: String = _
  var title: String = _
  var collection: String = _
  var estimated_date: String = _
  var identifier: String = _
  var copyright: String = _
  var media: String = _
  var tag: String = _
  var note: String = _
}

class ViewMap extends Bean {
  
  
  var remark: String = _
  var image_name: String = _
  var image_id: String = _
  var city: String = _
	var page_id: Long = _ // 仅VSH来源的照片
	var original_title: String = _
	var transliteration: String = _
	var alternative_orivinal_title: String = _
	var collection: String = _
	var digtized_file: String = _
	var map_type: String = _
	var authors: String = _
	var year: String = _
	var size: String = _
	var map_support: String = _
	var place_of_publication: String = _
	var repository: String = _
	var publishers: String = _
	val timestamp: Timestamp = new Timestamp(System.currentTimeMillis())
}

class Joseph extends Bean {
  var title: String = _
  var location: String = _
  var date: String = _
  var original_caption_by_joseph_needham: String = _
  var photographer: String = _
  var classmark: String = _
}

class Yenching extends Bean {
  
  @BeanProperty
  var page: Int = _
  
  @BeanProperty
  var work_id: Int = _
  
  @BeanProperty
  var source_id: Int = _
  
  @BeanProperty
  var image_name: String = _
  
  @BeanProperty
  var title: String = _
  
  @BeanProperty
  var author_or_creator: String = _
  
  @BeanProperty
  var description: String = _
  
  @BeanProperty
  var dimensions: String = _
  
  @BeanProperty
  var notes: String = _
  
  @BeanProperty
  var creation_date: String = _
  
  @BeanProperty
  var repository: String = _
  
  @BeanProperty
  var permalink: String = _
  
}

class WeiChat extends Bean {

  @BeanProperty
  var original_title: String = _
  
  @BeanProperty
  var image_name: String = _
  
  @BeanProperty
  var source: String = _
  
  @BeanProperty
  var remark: String = _
  
}

class Book extends Bean {

  @BeanProperty
  var id_item: String = _
  
  @BeanProperty
  var id_storekeeper: String = _
  
  @BeanProperty
  var id_store: String = _
  
  @BeanProperty
  var isbn: String = _

  @BeanProperty
  var title: String = _

  @BeanProperty
  var standard_title: String = _

  @BeanProperty
  var author: String = _

  @BeanProperty
  var publisher: String = _

  @BeanProperty
  var publish_date: String = _ // 年代 / 出版时间
  
  @BeanProperty
  var print_date: String = _ // 印刷时间

  @BeanProperty
  var cover: String = _ // 装帧

  @BeanProperty
  var size: String = _ // 开本

  @BeanProperty
  var edition: String = _ // 版次
  
  @BeanProperty
  var print_times: String = _ // 印次

  @BeanProperty
  var quality: String = _

  @BeanProperty
  var price: Int = _

  @BeanProperty
  var putaway_or_deal_date: String = _ // 上架日期
  
  @BeanProperty
  var deal_type: String = _ // 交易状态：上书 / 已售

  @BeanProperty
  var data_source: String = _ // kfz / by / dd / 

  @BeanProperty
  var bookstore: String = _
  
  @BeanProperty
  var location: String = _

  @BeanProperty
  var url: String = _
  
  @BeanProperty
  var url_store: String = _
  
  @BeanProperty
  var url_storekeeper: String = _
  
  @BeanProperty
  var batch: Int = _

  @BeanProperty
  var status: String = _ // normal 正常deleted 删除 

  @BeanProperty
  var `type`: String = _ // old 旧书 new 新书（有条码）
  
  @BeanProperty
  var orderby: String = _ // default 默认 putaway 上架时间
  
  @BeanProperty
  var keyword: String = _ 

  @BeanProperty
  val timestamp: Timestamp = new Timestamp(System.currentTimeMillis())

}

class BookGrid extends Bean {

  @BeanProperty
  var title: String = _
  
  @BeanProperty
  var year: String = _
  
  @BeanProperty
  var publisher: String = _
  
  @BeanProperty
  var author: String = _
  
  @BeanProperty
  var translator: String = _
  
  @BeanProperty
  var rating_nums: String = _
  
  @BeanProperty
  var comment_nums: String = _
  
  @BeanProperty
  var image: String = _
  
  @BeanProperty
  val timestamp: Timestamp = new Timestamp(System.currentTimeMillis())
}

object Test extends App {
  
      val retry = new RetryTemplate()
    //设置重试策略，主要设置重试次数
    val retryPolicy = new SimpleRetryPolicy()
    retryPolicy.setMaxAttempts(3)
    //设置重试回退操作策略，主要设置重试间隔时间
    val fixedBackOffPolicy = new FixedBackOffPolicy()
    fixedBackOffPolicy.setBackOffPeriod(3000)
    retry.setRetryPolicy(retryPolicy)
    retry.setBackOffPolicy(fixedBackOffPolicy)
    
    import scala.util.{Try, Success, Failure}

    var a : Try[org.jsoup.nodes.Document] = null
  var page_ : String = null
  println("enter")

  val recallPage = new RetryCallback[String, Exception] {
    override def doWithRetry(context: RetryContext): String = {

      a = Try(Jsoup.parse(new URL("http://search.kongfz.com/"), 1000))
      println("aaweras")
      println(a)
//      page_ = "123"
      // 该状态下发生异常，才会进行重试
//      if (null == a) {
//        println(">> retring ... ")
//        throw new RuntimeException // 抛出异常会触发重试
//      }
      a match {
        case Success(o) => println("ok")
        case Failure(o) =>
          println(">> retring ... ")
        throw new RuntimeException // 抛出异常会触发重试
      }
      page_
    }
  }

  // 重试失败后执行
  val recovery = new RecoveryCallback[String] {
    override def recover(context: RetryContext): String = {
      // 所有 retry 均失败后，程序中止
      println("stop...")
      null
    }
  }

  // 调用重试机制
  val page = retry.execute(recallPage, recovery)
}