package repositories

import scala.concurrent.Future

import repositories.poso.Super
import slick.lifted.CanBeQueryCondition

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-13 下午11:28:06
 * @Description 
 */
trait Repository[E <: Super[E]/*Entity*/, T/*Table*/] {
  def list(status_$active: Int = 0): Future[Seq[E]]
  def queryBy[C : CanBeQueryCondition](f_x:  => C): Future[E]
  
//  implicit val fxShow = (l: List[E]) => l foreach println
//  def peek(list: List[E], top: Int = -1)(implicit f_x: List[E] => Unit): List[E] = { 
//    if(top == -1) 
//      f_x(list)
//    else
//      f_x(list take(top))
//    list
//  }
}

abstract class AbstractRepository[E <: Super[E], T <: SuperTable_[E]]

