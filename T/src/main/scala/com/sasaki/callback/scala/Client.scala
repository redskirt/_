package com.sasaki.callback.scala

class Client(val server: Server) {
  // 定义回调函数
  def notice(status: String) = println("3) Server notice --> Server invoked process and return status. --> " + status)
  def invokeServer(message: String) = server.executeStatus(notice, message)
  
  // 简化，在调用server时直接定义并传入回调函数
  def invokeServer_(message: String) = 
    server.executeStatus(status => println("3) Server notice --> Server invoked process and return status. --> " + status), message)
}
 
class Server {
  def executeStatus(callback: String => Unit, message: String): Unit = {
		Thread.sleep(5000) // 模拟Server处理任务时间，耗费5s
		
		println("2) Service execute --> Server received message of client. --> " + message)
		val success = "200"
		callback(success) // 处理完后回调通知
  }
}
 
object Client {
  def main(args: Array[String]) {
    for(i <- 0 to 3) {
      println("1) Invoke Server --> Client invoke method of server.")
    	new Client(new Server()).invokeServer_("Do anthing..." + i)
    }
  
    while(true) Thread.sleep(500)
  }
}

