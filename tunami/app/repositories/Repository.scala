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
  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._
    
  def list(): Future[Seq[E]]
 // def queryList[C : CanBeQueryCondition](f_x: T => C): Future[Seq[E]]
  def queryList[C <: Rep[_]](f_x: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]]
//  def queryWithId(id: Long): Future[Option[E]]
  def querySingle[C : CanBeQueryCondition](f_x: T => C): Future[Option[E]]
//  def exists(id: Long): Futurse[Boolean]
  def exists[C : CanBeQueryCondition](f_x: T => C): Future[Boolean]
  def count(): Future[Int]

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
  
//  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._
  protected val q : TableQuery[T]
  
  override def list(): Future[Seq[E]] = db.run(q.result)
  override def queryList[C <: Rep[_]](f_x: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]] = db.run(q.withFilter(f_x).result)
//  override def queryWithId(id: Long): Future[Option[E]] = db.run(q.filter(_.id === id).result.headOption)
  override def querySingle[C : CanBeQueryCondition](f_x: T => C): Future[Option[E]] = db.run(q.withFilter(f_x(_)).result.headOption)
//  override def exists(id: Long): Future[Boolean] = db.run(q.filter(_.id === id).exists.result)
  override def exists[C : CanBeQueryCondition](f_x: T => C): Future[Boolean] = db.run(q.withFilter(f_x(_)).exists.result)
  override def count(): Future[Int] = db.run(q.length.result)
  
//  def withFilter[C : CanBeQueryCondition](f: T => C) = q.withFilter(f).result
//  def queryFilter(fi: String): Future[Seq[E]] = db.run(q.filter(o => o.username == fi).result)
  
}

