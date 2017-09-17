package repositories

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import java.sql.Timestamp

trait RepositoryUtil {
  lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._
  
  abstract class SuperTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def timestamp = column[Timestamp]("timestamp")
  }
  
  class A {
    
  }
}

object RepositoryUtil {
	type TSuperTable[T] = SuperTable[T]
	
}