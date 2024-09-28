package chapter3

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala._

/**
 * 获取每个传感器最高的温度保存到MySQL数据库中
 */
object CustomSink {
  def main(args: Array[String]): Unit = {
    //获取运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    //数据随机生成
    val inputStream = env.addSource(new SensorSource(10))
    // 三元组转换为二元组 （Sting,Double） (传感器ID，温度)
    val dataStream: DataStream[(String, Double)] = inputStream
      .map(sensorReading => (
        sensorReading._1, sensorReading._3
      ))
      // 根据传感器ID分组
      .keyBy(_._1)
      //传感器ID，每个传感器最高温度
      .reduce((curSensorReading, newSensorReading) => (curSensorReading._1, curSensorReading._2.max(newSensorReading._2)))
    //添加到Sink
    dataStream.addSink(new MyJdbcSink())
    //开始执行
    env.execute()
  }

  /**
   *
   * 自定义Sink 继承RichSinkFunction
   *
   * 实现将结果数据输出到MySQL数据库
   *
   */
  class MyJdbcSink() extends RichSinkFunction[(String, Double)] {
    // 定义JDBC连接相关的信息
    var conn: Connection = _
    var insertStmt: PreparedStatement = _
    var updateStmt: PreparedStatement = _
    val url = "jdbc:mysql://localhost:3306/flink_project" //JDBC连接
    val user = "root" //数据库用户名
    val password = "root123456" //数据库密码
    val insertSql = "insert into sensor_data (id,temperature) values (?,?)" //插入SQL
    val updateSql = "update sensor_data set temperature = ? where id = ?" //更新SQL

    // 初始化
    override def open(parameters: Configuration): Unit = {
      //获取JDBC连接
      conn = DriverManager.getConnection(url, user, password)
      insertStmt = conn.prepareStatement(insertSql)
      updateStmt = conn.prepareStatement(updateSql)
    }

    // 调用
    override def invoke(value: (String, Double)): Unit = {
      // 执行更新语句
      updateStmt.setDouble(1, value._2)
      updateStmt.setString(2, value._1)
      updateStmt.execute()
      // 如果没有更新数据，那么执行插入操作
      if (updateStmt.getUpdateCount == 0) {
        insertStmt.setString(1, value._1)
        insertStmt.setDouble(2, value._2)
        insertStmt.execute()
      }
    }

    // 关闭操作
    override def close(): Unit = {
      insertStmt.close()
      updateStmt.close()
      conn.close()
    }
  }
}

