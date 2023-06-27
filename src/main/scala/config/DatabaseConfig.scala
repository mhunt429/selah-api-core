package  config
import cats.syntax.functor.*
import cats.effect.{Async, IO, Resource, Sync}
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway
import doobie.util.ExecutionContexts

import scala.concurrent.ExecutionContext
case class DatabaseConnectionsConfig(poolSize: Int)
case class DatabaseConfig(
                           url: String,
                           driver: String,
                           user: String,
                           password: String,
                           connections: DatabaseConnectionsConfig,
                         )

object DatabaseConfig {
  def dbTransactor[F[_] : Async](dbc: DatabaseConfig): Resource[F, HikariTransactor[F]] =
    Resource.eval(ExecutionContexts.cachedThreadPool[F]).flatMap { connEc =>
      Resource.make {
        Async[F].delay {
          HikariTransactor.newHikariTransactor[F](
            dbc.driver,
            dbc.url,
            dbc.user,
            dbc.password,
            connEc
          )
        }
      } { transactor =>
        transactor.map(_ => _)
      }
    }

  /**
   * Runs the flyway migrations against the target database
   */
  def initializeDb[F[_]](cfg: DatabaseConfig)(implicit S: Sync[F]): F[Unit] =
    S.delay {
      val fw: Flyway =
        Flyway
          .configure()
          .dataSource(cfg.url, cfg.user, cfg.password)
          .load()
      fw.migrate()
    }.as(())
}
