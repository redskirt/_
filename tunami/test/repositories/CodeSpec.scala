package repositories

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import poso.Account
import scala.reflect.NameTransformer

@RunWith(classOf[JUnitRunner])
class CodeSpec extends FunSuite with BeforeAndAfter {

  test("Constructor") {
    val account = Account("username", "password")._mail("email")._status(0)._typee(0)
    println(account.mail)

  }

  test("md5") {
    val text = "a"
    import java.security.MessageDigest
    val digest = MessageDigest.getInstance("MD5")
    val md5 = digest.digest(text.getBytes).map("%02x".format(_)).mkString
    println(md5)
  }
}