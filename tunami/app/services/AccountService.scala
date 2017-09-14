package services

import repositories.AccountRepository
import javax.inject.{ Inject, Singleton }
import poso.Account
import scala.concurrent.Await
import scala.concurrent.duration.Duration


/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-13 下午2:47:48
 * @Description 
 */
@Singleton
class AccountService @Inject() (accountRepository: AccountRepository) {
  
  def createAccount(a: Account): Int = {
    println(accountRepository)
    accountRepository.create(a.username, a.password)//.value.get.getOrElse(new poso.Super[Account]).id
    1
  }
  
  def insertAccount(a: Account) = {
    val result = accountRepository.insert(a)
    println(result)
    result
  }
}
