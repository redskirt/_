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

  }

  test("md5") {
    val text = "a"
    import java.security.MessageDigest
    val digest = MessageDigest.getInstance("MD5")
    val md5 = digest.digest(text.getBytes).map("%02x".format(_)).mkString
    println(md5)
  }
}