package chapter2

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.streaming.api.scala._
/**
 * 批量单词统计
 */
object BatchWordCount {
  def main(args: Array[String]): Unit = {
    //获取执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment
    //文件的路径
    val filePath = "data/wordcount.txt"
    //读取文件返回Dataset
    val inputDataSet: DataSet[String] = env.readTextFile(filePath)

    val wordCountDataSet = inputDataSet
      //按照空格进行分词
      .flatMap(_.split(" "))
      // _代表单词 (_,1)二元组
      .map((_, 1))
      // 根据第1个字段，即单词进行分组
      .groupBy(0)
      //求和，根据第2个字段，即数量进行求和
      .sum(1)

    wordCountDataSet.print()

  }

}
