package services

import scala.concurrent.Future

import javax.inject.Inject
import javax.inject.Singleton
import poso.Account
import repositories.AccountRepository


/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-13 下午2:47:48
 * @Description 
 */
@Singleton
class AccountService @Inject() (accountRepository: AccountRepository) {

  def createAccount(a: Account): Future[Int] = accountRepository.insert(a)
  
}
