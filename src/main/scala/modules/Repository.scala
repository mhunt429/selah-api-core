package modules
import cats.Monad
import doobie.util.transactor.Transactor
import cats.effect.*
import cats.implicits.*
import infrastructure.repository.HealthCheckRepositoryImpl
import doobie.implicits.*

import scala.concurrent.ExecutionContext

//Dependency injection for our data access repository layers
object Repository {
  def make(xa: Transactor[IO]): Repository = {
    new Repository(xa)
  }
}

class Repository (xa: Transactor[IO]) {
  val healthCheckRepository: HealthCheckRepositoryImpl = new HealthCheckRepositoryImpl(xa)
}