package repositories

import scala.concurrent.Future

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import repositories.RepositoryUtil.SuperTable
import repositories.poso.Super
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.CanBeQueryCondition

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-13 下午11:28:06
 * @Description 
 */
trait Repository[E/*Entity*/, T/*Table*/] {
  def list(): Future[Seq[T]]
  def queryBy[C : CanBeQueryCondition](f_x: T => C): Future[Seq[T]]
  
//  implicit val fxShow = (l: List[E]) => l foreach println
//  def peek(list: List[E], top: Int = -1)(implicit f_x: List[E] => Unit): List[E] = { 
//    if(top == -1) 
//      f_x(list)
//    else
//      f_x(list take(top))
//    list
//  }
}

abstract class AbstractRepository[E <: Super[E], T <: SuperTable[T]] extends Repository[E, T] with HasDatabaseConfig[JdbcProfile] {
  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._
  protected val t__ : TableQuery[T]
  
  override def list(): Future[Seq[T]] = db.run(t__.result)
  override def queryBy[C : CanBeQueryCondition](f_x: T => C): Future[Seq[T]] = db.run(t__.withFilter(f_x).result)
  def querySingle[C : CanBeQueryCondition](f_x: T => C): Future[Option[T]] = db.run(t__.withFilter(f_x).result.headOption)
  
}

