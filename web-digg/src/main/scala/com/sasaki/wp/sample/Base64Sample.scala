package com.sasaki.wp.sample

import java.io.FileInputStream
import com.sasaki.wp.persistence.QueryHelper

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 14, 2018 6:56:36 PM
 * @Description
 */
object Base64Sample extends QueryHelper {

  def main(args: Array[String]): Unit = {
    val fileName = "/Users/sasaki/Desktop/c.jpg"
    val inputStream = new FileInputStream(fileName)
    val bytes = new Array[Byte](inputStream.available())
    inputStream.read(bytes)
    inputStream.close()

    val encoder = new sun.misc.BASE64Encoder()

    val base64Image = encoder.encode(bytes).replace("\n", "")

    println(base64Image)

    import com.sasaki.wp.persistence.poso._

    saveSource(Source(1, ""))

  }
}