//转换为UserScore对象
val stream2=stream.map(json=>{
  val jsonObj: JSONObject = JSON.parseObject(json)
  //用户
  val user: String = jsonObj.getString("user")
  //课程
  val course: String = jsonObj.getString("course")
  //分数
  val score: Int = jsonObj.getIntValue("score")
  //构造对象
  UserScore(user,course,score)