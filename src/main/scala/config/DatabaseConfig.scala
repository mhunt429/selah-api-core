package config

import cats.syntax.functor.*
import cats.effect.{Async, IO, Resource, Sync}
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway
import doobie.util.ExecutionContexts
import scala.concurrent.ExecutionContext

case class DatabaseConfig(
                           url: String,
                           driver: String,
                           user: String,
                           password: String,
                           connections: Int,
                         )

object DatabaseConfig {
  def transactor(config: DatabaseConfig, executionContext: ExecutionContext): Resource[IO, HikariTransactor[IO]] = {
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
        val flyWay = Flyway.configure().dataSource(dataSource).load()
        flyWay.migrate()
        ()
      }
    }
  }
}
