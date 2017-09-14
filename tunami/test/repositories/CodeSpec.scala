package repositories

import org.scalatest.BeforeAndAfter
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import poso.Account

@RunWith(classOf[JUnitRunner])  
class CodeSpec extends FunSuite with BeforeAndAfter  {
  
  test("Constructor") {
    val a = Account("a", "b")._mail("email")
    println(a.mail)
  }
}