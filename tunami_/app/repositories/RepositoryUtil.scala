package repositories

import java.sql.Timestamp

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

class RepositoryUtil {

}

object RepositoryUtil {
  lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._

  abstract class SuperTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
//    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
//    def timestamp = column[Timestamp]("timestamp")
  }
}