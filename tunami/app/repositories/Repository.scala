package repositories

import scala.concurrent.Future

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import repositories.RepositoryUtil.SuperTable
import repositories.poso.Clazz
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
  def list(): Future[Seq[E]]
  def queryList[C : CanBeQueryCondition](f_x: T => C): Future[Seq[E]]
  def queryWithId(id: Long): Future[Option[E]]

}

abstract class AbstractRepository[E <: Clazz[E], T <: SuperTable[E]]() extends Repository[E, T] with HasDatabaseConfig[JdbcProfile] {
  
  //  implicit val fxShow = (l: List[E]) => l foreach println
//  def peek(list: List[E], top: Int = -1)(implicit f_x: List[E] => Unit): List[E] = { 
//    if(top == -1) 
//      f_x(list)
//    else
//      f_x(list take(top))
//    list
//  }
  
  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._
  protected val q : TableQuery[T]
  
  override def list(): Future[Seq[E]] = db.run(q.result)
  override def queryList[C : CanBeQueryCondition](f_x: T => C): Future[Seq[E]] = db.run(q.withFilter(f_x).result)
  override def queryWithId(id: Long): Future[Option[E]] = db.run(q.filter(_.id === id).result.headOption)
  
  def querySingle[C : CanBeQueryCondition](f_x: T => C): Future[Option[E]] = db.run(q.withFilter(f_x).result.headOption)
  
}

