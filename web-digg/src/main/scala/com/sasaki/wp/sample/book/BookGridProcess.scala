package com.sasaki.wp.sample.book

import com.sasaki.wp.persistence.QueryHelper
import org.jsoup.Jsoup
import java.io.File
import com.sasaki.wp.util.HttpDownload
import com.sasaki.wp.persistence.poso.BookGrid

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Dec 6, 2018 11:00:14 PM
 * @Description
 */

class BookGridProcess {

}

object BookGridProcess extends QueryHelper {

  def main(args: Array[String]): Unit = {

    val document = Jsoup.parse(new File("/Users/sasaki/git/_/web-digg/src/main/resources/book-grid.txt"), "utf-8")
    val results = document.getElementsByClass("result")
    for (i <- 146 until results.size) {
      val id = i + 1
      val result = results.get(i)
      val img = result.getElementsByTag("img").first().attr("src")
            println(img)
      val imageName = s"/Users/sasaki/bigbook/grid/grid-$id.jpg"
      HttpDownload.download(img, imageName)
      val title = result
        .getElementsByClass("title")
        .first()
        .getElementsByTag("a")
        .first()
        .text()
      val rating_nums = result.getElementsByClass("rating_nums").text()
      val comment_nums = result.getElementsByTag("span").get(2).text()
      val subject_cast = result
        .getElementsByClass("subject-cast")
        .first()
      if (subject_cast != null) {
        val array = subject_cast.text()
          .split("/")
          .map(_.trim)
        if (array.size == 4) {
          val author = array(0)
          val translator = array(1)
          val publisher = array(2)
          val year = array(3)

          val o = new BookGrid
          o.id = id
          o.setTitle(title)
          o.setAuthor(author)
          o.setTranslator(translator)
          o.setPublisher(publisher)
          o.setYear(year)
          o.setRating_nums(rating_nums)
          o.setComment_nums(comment_nums)
          o.setImage(imageName)
          saveBookGrid(o)
        }
      }
    }
  }
}