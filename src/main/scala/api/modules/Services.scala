package api.modules

import application.services.HealthCheckServiceImpl
import application.services.account.{RegistrationServiceImpl, UserServiceImpl}
import application.services.connector.{ConnectorService, PlaidHttpService}
import application.services.identity.IdentityService
import application.services.security.{CryptoService, TokenService}
import core.config.Config
import org.hashids.Hashids
//Dependency injection for application service layer
object Services {
  def make(repository: Repository, config: Config): Services = {
    Services(repository, config)
  }
}

class Services private (repository: Repository, config: Config) {
  import concurrent. ExecutionContext. Implicits. global
  val cryptoService: CryptoService =
    CryptoService(config, new Hashids(config.securityConfig.hashIdSalt, 24))

  val tokenService: TokenService = TokenService(config)

  val healthCheckService: HealthCheckServiceImpl =
    HealthCheckServiceImpl(repository.healthCheckRepository)

  val userService: UserServiceImpl = UserServiceImpl(
    repository.appUserRepository,
    cryptoService
  )

  val plaidHttpService: PlaidHttpService = PlaidHttpService(config.plaidConfig)

  val registrationService: RegistrationServiceImpl = RegistrationServiceImpl(
    repository.accountRepository,
    repository.appUserRepository,
    cryptoService,
    tokenService
  )
  
  val identityService = IdentityService(cryptoService,tokenService,  repository.appUserRepository)
  
  val connectorService = ConnectorService(plaidHttpService, cryptoService, repository.accountConnectorRepository)
}
