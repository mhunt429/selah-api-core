package modules

import application.sevices.HealthCheckServiceImpl
import cats.effect.kernel.Async
import cats.effect.*

//Dependency injection for application service layer
object Services {
  def make[F[_]: Async](repository: Repository[F]): Services[F] = {
    new Services[F](repository) {}
  }
}

sealed abstract  class Services[F[_]: Async] private (
 repository: Repository[F]) {
  val healthCheckService: HealthCheckServiceImpl[F] = 
    HealthCheckServiceImpl[F](repository.healthCheckRepository)
}