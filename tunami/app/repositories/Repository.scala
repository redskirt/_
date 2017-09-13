package repositories

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-13 下午11:28:06
 * @Description 
 */
trait Repository[T] {
  def list[T](): List[T]
  
  implicit val fxShow = (l: List[T]) => l foreach println
  def peek[T](list: List[T], top: Int = -1)(implicit f_x: List[T] => Unit): List[T] = { 
    if(top == -1) 
      f_x(list)
    else
      f_x(list take(top))
    list
  }
  
}