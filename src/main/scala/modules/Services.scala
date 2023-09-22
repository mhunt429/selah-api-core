package modules

import application.sevices.{HealthCheckServiceImpl, SecurityService, UserServiceImpl}
import cats.effect.*
import cats.effect.kernel.Async
import config.*
import modules.*
import org.hashids.Hashids
//Dependency injection for application service layer
object Services {
  def make(repository: Repository, config: Config): Services = {
    Services(repository, config)
  }
}

class Services private (repository: Repository, config: Config) {
  val healthCheckService: HealthCheckServiceImpl =
    HealthCheckServiceImpl(repository.healthCheckRepository)
  val userService: UserServiceImpl = UserServiceImpl(
    repository.appUserRepository,
    SecurityService(new Hashids(config.securityConfig.hashIdSalt, 24))
  )
}
