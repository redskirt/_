package com.sasaki.wp.persistence.poso

import java.sql.Timestamp
import org.squeryl.annotations.Column

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
  
  val city: String) extends Bean {
  var content: String = _
  @Column("source_image_name") 
  var sourceImageName: String = _
  @Column("base64_image") 
  var base64Image: String = _
  @Column("image_name") 
  var imageName: String = _
  @Column("image_id") 
  var imageId: String = _
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

