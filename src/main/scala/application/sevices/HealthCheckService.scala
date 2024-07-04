package application.sevices

import cats.effect.*
import cats.effect.implicits.*
import cats.syntax.all.*
import core.models.{HealthCheck, PostgreSQL}
import infrastructure.repository.HealthCheckRepository
import org.log4s.*

import scala.concurrent.duration.*
trait HealthCheckService {
  def status: IO[HealthCheck]
}

class HealthCheckServiceImpl(healthCheckRepository: HealthCheckRepository)
    extends HealthCheckService {
  private val logger = org.log4s.getLogger
  def status: IO[HealthCheck] = {
    healthCheckRepository.getPostgresProcessId
      .flatMap {
        case Some(_) => HealthCheck(PostgreSQL(ok = true)).pure
        case None    => HealthCheck(PostgreSQL(ok = false)).pure
      }
      .handleErrorWith(error => {
        logger.error(s"Postgres response with error => ${error.getMessage}")
        IO(HealthCheck(PostgreSQL(ok = false)))
      })
  }
}
