package com.sasaki.wp.sample

import com.sasaki.wp.persistence.QueryHelper
import org.jsoup.Jsoup
import java.io.File
import com.sasaki.wp.persistence.poso.Joseph
import com.sasaki.wp.util.HttpDownload

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Sep 3, 2018 4:44:35 PM
 * @Description
 */
object WebDiggNRI extends QueryHelper {

  def main(args: Array[String]): Unit = {

    //    var a = 100d
    //    for(i <- 0 until 12) {
    //      	println(i + 1 + "月： " + a)
    //       a += (100 * Math.pow(1.3, i+1))
    //    }
    //    println("13月：" + a)
    //    println(a*129)

    val document = Jsoup.parse(new File("/Users/sasaki/Desktop/sc.html"), "utf-8")
    val table = document.getElementsByTag("table").get(1)
    val trs = table.getElementsByTag("tr")
    
    println(trs.size())

    for (i <- 0 until trs.size()) {
      val tr = trs.get(i)
      val tds = tr.getElementsByTag("td")
      val img = tds.get(1).child(0).attr("src")
      val text = tds.get(0).text()
      val array = text
        .split('|')
        .filter(o => o != null && o.length > 5)
        .map { _.split(':')(1) }
      val o = new Joseph
      o.title = array(0)
      o.location = array(1)
      o.date = array(2)
      o.original_caption_by_joseph_needham = array(3)
      o.photographer = array(4)
      o.classmark = img.substring(img.lastIndexOf("/") + 1, img.lastIndexOf("."))
      val name = img.substring(img.lastIndexOf("/") + 1, img.length())
//      HttpDownload.download(s"http://www.nri.cam.ac.uk/JN_wartime_photos/$img", s"/Users/sasaki/git/doc/kj/joseph/$name")
//      saveJoseph(o)
    }
  }
}

