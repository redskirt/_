package com.sasaki.isp.util

import java.util.regex.Pattern

/**
 *
 */
object Util {
  
  
  def nonNull(o: Any) = null != o

  def getMatched(str: String, regex: String): String = {
    val pattern = Pattern.compile(regex, Pattern.DOTALL)
    val matcher = pattern.matcher(str)
    
    if (matcher.find())
      return matcher.group(1)
    ""
  }

  def nonEmpty(o: Any) = 
    nonNull(o) && (o.getClass().getSimpleName match {
      case "String" => o != ""
//      case "???" => 
      case _ => false
    })
  
  
  def listFiles(url: String) = {
    import java.io.File

    val t = new File(url)
    if (t.isDirectory())
      t.listFiles().filterNot(_.isDirectory())
    else
      Array(t)
  }
  
  def writeFile(fileNameWithPath: String, content: String) = {
    import java.io.{ File, FileWriter, BufferedWriter }
    
    val file = new File(fileNameWithPath)
    if(file.exists())
      file.delete()
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write(content)
    writer.close()
  }
     
  def readTextFile(fileNameWithPath: String): Seq[String] = 
      scala.io.Source.fromFile(fileNameWithPath).getLines().toSeq
  
  def main(args: Array[String]): Unit = {
//    println(Util.prop("kafka.metadata.broker.list"))
//    println(hasConstants("jdbc.url", "" ))
//    println(_prop_.containsKey("jdbc.url"))
    // 北京缺 dbImage_ID-19023_No-1.jpeg
    listFiles("/Users/sasaki/vsh/bj")
    .filter(_.getName.contains("dbImage"))
//    .foreach { o => 
//      val name = o.getName
//      println(name)
//      println(name.substring(11, name.lastIndexOf("_")))
//    }
    .foreach { o =>
      if(200 > o.length()) {
        println(o.getName)
        o.delete()
      }
    }
    
  }
  
  
}