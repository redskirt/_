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
  @Column("page_id") val pageId:           Long,
  val content:                             String,
  @Column("base64_image") val base64Image: String) extends Bean {
  @Column("image_name")
  var imageName: String = _
  var timestamp: Timestamp = new Timestamp(System.currentTimeMillis())
}

case class ShView(
  @Column("page_id") val pageId:                             Long,
  @Column("image_id") val imageId:                           Long,
  val title:                                                 String,
  val collection:                                            String,
  val location:                                              String,
  val year:                                                  String,
  val date:                                                  String,
  @Column("estimated_date") val estimatedDate:               String,
  @Column("image_type") val imageType:                       String,
  @Column("material_form_of_image") val materialFormOfImage: String,
  @Column("private_repository") val privateRepository:       String,
  val notes:                                                 String,
  @Column("keywords_en") val keywordsEn:                     String,
  @Column("keywords_fr") val keywordsFr:                     String,
  @Column("street_name") val streetName:                     String,
  val repository:                                            String,
  val building:                                              String,
  @Column("related_image") val relatedImage:                 String) extends Bean {
  @Column("image_name") var imageName: String = _
  @Column("base64_image") var base64Image: String = _
  var timestamp: Timestamp = new Timestamp(System.currentTimeMillis())
}

