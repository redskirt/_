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
  val pageId:                               Long,
  val content:                             String,
  @Column("base64_image") 
  val base64Image: String) extends Bean {
  @Column("image_name")
  var imageName: String = _
  var timestamp: Timestamp = new Timestamp(System.currentTimeMillis())
}


