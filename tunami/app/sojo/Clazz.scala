package sojo

import java.sql.Timestamp

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-11 上午11:38:41
 * @Description 
 */

class Super[T] {
  var id: Int = _
  var timestamp: Timestamp = new Timestamp(System.currentTimeMillis())
  
  def _id(id: Int) = { this.id = id; this}
}

case class Account(username: String, password: String) extends Super[Account]{
    var mail: String = _
    var type_ : String = "0"
    var status: String = "0"
       
}
