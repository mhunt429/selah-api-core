package api.modules

import cats.Monad
import cats.effect.*
import cats.implicits.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import infrastructure.repository.{AccountRepositoryImpl, AppUserRepositoryImpl, HealthCheckRepositoryImpl}

import scala.concurrent.ExecutionContext

//Dependency injection for our data access repository layers
object Repository {
  def make(xa: Transactor[IO]): Repository = {
    new Repository(xa)
  }
}

class Repository(xa: Transactor[IO]) {
  val healthCheckRepository: HealthCheckRepositoryImpl =
    new HealthCheckRepositoryImpl(xa)
    
  val appUserRepository: AppUserRepositoryImpl = new AppUserRepositoryImpl(xa)
  
  val accountRepository: AccountRepositoryImpl = new AccountRepositoryImpl(xa)
}
