package repositories

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import poso.Account
import org.specs2.mock.Mockito
import services.AccountService
import javax.inject.Inject
import poso.Account


/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-09-11 下午4:13:53
 * @Description
 */
@RunWith(classOf[JUnitRunner])
class UnitSpec extends Specification with Mockito {
  
//  "Account" should {
//    "have a name" in {
//      val account = Account("Sasaki", "xxx")
//      account.username must beEqualTo("Sasaki")
//    }
//  }
  
//  "AccountService$createAccount" should {
//	  "cratea account" in {
//	    val accountRepository = mock[AccountRepository]
//	    accountRepository.create("Sasaki", "xxx")
//	    
//	    val accountService = new AccountService(accountRepository)
//	    val accountId = accountService.createAccount(Account("Sasaki", "xxx"))
//	    accountId != 0 must beTrue
//	  }
//  }
  
//  "AccountService$insertAccount" should {
//	  "insert account" in {
//	    val accountRepository = mock[AccountRepository]
//	    val accountService = new AccountService(accountRepository)
//	    val result = accountService.createAccount(Account("Sasaki", "xxx"))
//	    result.value.get.get must beEqualTo(1)
//	  }
//  }
  

}
