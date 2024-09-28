package chapter3

import org.apache.flink.connector.jdbc.{JdbcConnectionOptions, JdbcSink, JdbcStatementBuilder}
import org.apache.flink.streaming.api.scala._
import java.sql.PreparedStatement

object JdbcSinkTest {
  def main(args: Array[String]): Unit = {
    //获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    // 从集合中读取数据
    val dataStream: DataStream[PageView] = env.fromElements(
      PageView(1, 1547718100, 1, "/index.html", 10),
      PageView(2, 1547718200, 2, "/index.html", 20),
      PageView(3, 1547719300, 3, "/index.html", 10),
      PageView(4, 1547720300, 1, "/goods.html", 100),
      PageView(5, 1547720600, 2, "/cart.html", 30)
    )

    dataStream.addSink(JdbcSink.sink(
      "INSERT INTO page_view (user_id, visit_url,visit_time) VALUES (?, ?,?)", //  定义写入MySQL的语句
      new JdbcStatementBuilder[PageView] {
        override def accept(t: PreparedStatement, u: PageView): Unit = {
          t.setInt(1, u.userId)
          t.setString(2, u.visitUrl)
          t.setInt(3, u.visitTime)
        }
      },
      new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
        .withUrl("jdbc:mysql://localhost:3306/flink_project??useSSL=false")
        .withDriverName("com.mysql.jdbc.Driver")
        .withUsername("root")
        .withPassword("root123456")
        .build()
    ))

    //开始执行
    env.execute()
  }
}
