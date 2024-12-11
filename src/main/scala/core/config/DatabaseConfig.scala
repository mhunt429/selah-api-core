package core.config

import cats.effect.{IO, Resource}
import cats.syntax.functor.*
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

case class DatabaseConfig(
    url: String,
    driver: String,
    user: String,
    password: String,
    connections: Int
)

object DatabaseConfig {
  def transactor(
      config: DatabaseConfig,
      executionContext: ExecutionContext
  ): Resource[IO, HikariTransactor[IO]] = {
    HikariTransactor.newHikariTransactor[IO](
      config.driver,
      config.url,
      config.user,
      config.password,
      executionContext
    )
  }

  def initialize(transactor: HikariTransactor[IO]): IO[Unit] = {
    transactor.configure { dataSource =>
      IO {
        val flyWay = Flyway
          .configure()
          .mixed(true)
          .dataSource(dataSource)
          .cleanDisabled(true)
          .outOfOrder(false)
          .load()
        flyWay.migrate()
        ()
      }
    }
  }

 /*
 Commenting this out because I'm figuring out issues with multiple tests
  def cleanSchema(transactor: HikariTransactor[IO]): IO[Unit] = {
    transactor.configure { dataSource =>
      IO {
        val flyWay = Flyway
          .configure()
          .mixed(true)
          .dataSource(dataSource)
          .cleanDisabled(false)
          .outOfOrder(true)
          .load()
        flyWay.clean()
        flyWay.migrate()
        ()
      }
    }
  }*/
}
