package repositories

import java.io.File

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.Finders
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import javax.inject.Singleton
import play.api.Application
import play.api.Environment
import play.api.Mode
import play.api.Play
import play.api.inject.guice.GuiceApplicationBuilder
import slick.lifted.CanBeQueryCondition
import slick.lifted.Rep

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Description
 */

@RunWith(classOf[JUnitRunner])
class StaticSpec extends FunSuite with BeforeAndAfter {
  
  lazy val application = new GuiceApplicationBuilder()
    .in(Environment(new File("path/to/app"), this.getClass.getClassLoader, Mode.Test))
    .build()
    
  lazy val accountRepository = Application.instanceCache[AccountRepository].apply(application)  
  
  before {
    Play.start(application)
  }

  test("An empty Set should have size 0") {
    assert(Set.empty.size == 0)
  }

  test("query") {
      implicit val BooleanColumnCanBeQueryCondition : CanBeQueryCondition[Rep[Boolean]] =
    new CanBeQueryCondition[Rep[Boolean]] {
      def apply(value: Rep[Boolean]) = value
    } 
    
    val inf = Duration.Inf
//   Await.result(accountRepository.list(), inf).foreach(println)    
//    println(Await.result(accountRepository.queryList(a => a.id === 1), inf))
//    println(Await.result(accountRepository.count(), inf))
//    println(Await.result(accountRepository.querySingle { x => x.id === 1 }, inf)) 
//    println(Await.result(accountRepository.queryWithId(1), inf))
  }

  after(Play.stop(application))

}