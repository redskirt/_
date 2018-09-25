package com.sasaki.wp.sample

import com.sasaki.wp.util.Util
import com.sasaki.wp.persistence.poso.WeiChat
import com.sasaki.wp.persistence.QueryHelper

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Sep 25, 2018 3:16:54 PM
 * @Description 
 */
object WebDiggWeiChat extends QueryHelper {
  
  def main(args: Array[String]): Unit = {
    
    var offset = 6816

    val subDirs = Util.listFiles("/Users/sasaki/git/doc/kj/wx/wtyx", true)
//      .take(10)
      .foreach { o =>
        val subUrl = o.toString()
        val dirName = o.getName.replace("kj", "")
        Util.listFiles(subUrl)
//          .take(10)
          .foreach { p =>
            val imageName = s"$offset.jpg"
            val w = new WeiChat
            w.id = offset
            w.setOriginal_title(dirName)
            w.setImage_name(imageName)
            w.setSource("外滩以西")
            saveWeiChat(w)
            
            p.renameTo(new java.io.File(s"/Users/sasaki/git/doc/kj/wx/$offset.jpg"))
            offset += 1
          }
      }
    
    
  }
  
}