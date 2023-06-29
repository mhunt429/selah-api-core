package modules

import application.sevices.HealthCheckServiceImpl
import cats.effect.kernel.Async
import cats.effect._
import modules._


//Dependency injection for application service layer
object Services {
  def make(repository: Repository): Services = {
    new Services(repository)
  }
}

 class Services private (repository: Repository) {
  val healthCheckService: HealthCheckServiceImpl =
    HealthCheckServiceImpl(repository.healthCheckRepository)
}