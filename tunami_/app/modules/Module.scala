package modules

import java.time.Clock
import com.google.inject.{Provides, AbstractModule}
import repositories._
import repositories.AccountRepository.TAccount
import repositories.poso.Account
import com.google.inject.name.Names

/**
 * @Author Wei Liu
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-09-21 下午5:32:23
 * @Description
 */
class Module extends AbstractModule {

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    
//        bind(classOf[AbstractRepository[Account, TAccount]])
//    .annotatedWith(Names.named("accountRepository"))
//    .to(classOf[AccountRepository])
//    .asEagerSingleton()
    
  }

  @Provides
  def accountRepository: Repository[Account, TAccount] = new AbstractRepository[Account, TAccount] {
    override protected val q : dbConfig.driver.api.TableQuery[TAccount] = AccountRepository.t_account
  }

}
