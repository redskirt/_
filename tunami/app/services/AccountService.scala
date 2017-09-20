package services

import javax.inject.Inject
import javax.inject.Singleton
import repositories.Repository
import repositories.AccountRepository.TAccount
import repositories.poso.Account
import scala.concurrent.ExecutionContext


/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-13 下午2:47:48
 * @Description 
 */
@Singleton
class AccountService @Inject() (accountRepository: Repository[Account, TAccount])(implicit exec: ExecutionContext) {
  
  def queryAll() = accountRepository.list()

  
}

