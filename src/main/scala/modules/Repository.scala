package  modules
import application.sevices.HealthCheckServiceImpl
import doobie.util.transactor.Transactor
import cats.effect.*
import infrastructure.repository.{HealthCheckRepository, HealthCheckRepositoryImpl}

import scala.concurrent.ExecutionContext

//Dependency injection for our data access repository layers
object Repository {
  def make[F[_]: Async](xa: Transactor[F]): Repository[F] = {
    new Repository[F](xa) {}
  } 
}

sealed abstract class Repository[F[_]: Async] private (
      xa: Transactor[F]){
val healthCheckRepository: HealthCheckRepositoryImpl[F] = HealthCheckRepositoryImpl[F](xa)
}