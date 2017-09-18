package com.sasaki.o

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-08-28 下午2:36:02
 * @Description 高阶函数文件查找示例
 */
class FileMatcher {
  private def filesHere = (new java.io.File(".")).listFiles

  def filesMatching(matcher: String => Boolean) = {
    for (file <- filesHere; if matcher(file.getName))
      yield file
  }

  def filesEnding(query: String) = filesMatching(o => o.endsWith(query))

  def filesContaining(query: String) = filesMatching(_.contains(query))

  def filesRegex(query: String) = filesMatching(_.matches(query))
}