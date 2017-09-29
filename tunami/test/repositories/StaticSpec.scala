package repositories

import java.io.File

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite

import play.api.Application
import play.api.Environment
import play.api.Mode
import play.api.Play
import play.api.inject.guice.GuiceApplicationBuilder
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite

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

  test("test1") {
   Await.result(accountRepository.list(), Duration.Inf).foreach(println)    
  }

  after(Play.stop(application))

}