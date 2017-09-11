package persistence

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-11 下午3:03:27
 * @Description 
 */
@Singleton
class AccountPersistence @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  
  import dbConfig._
  import profile.api._
  
  
}
