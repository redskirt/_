package services

import repositories.AccountRepository
import javax.inject.Inject
import poso.Account


/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-13 下午2:47:48
 * @Description 
 */
class AccountService @Inject() (accountRepository: AccountRepository) {
  
  def createAccount(a: Account): Int = {
    accountRepository.create(a.username, a.password).value.get.getOrElse(new poso.Super[Account]).id
  }
}
