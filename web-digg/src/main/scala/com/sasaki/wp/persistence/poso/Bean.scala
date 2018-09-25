package com.sasaki.wp.persistence.poso

import java.sql.Timestamp
import org.squeryl.annotations.Column
import scala.beans.BeanProperty

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