package api.modules

import application.sevices.{HealthCheckServiceImpl, SecurityService, UserServiceImpl}
import cats.effect.*
import core.config.Config
import org.hashids.Hashids
//Dependency injection for application service layer
object Services {
  def make(repository: Repository, config: Config): Services = {
    Services(repository, config)
  }
}

class Services private (repository: Repository, config: Config) {
  val securityService: SecurityService =
    SecurityService(new Hashids(config.securityConfig.hashIdSalt, 24), config)

  val healthCheckService: HealthCheckServiceImpl =
    HealthCheckServiceImpl(repository.healthCheckRepository)
  val userService: UserServiceImpl = UserServiceImpl(
    repository.appUserRepository,
    securityService
  )
}
