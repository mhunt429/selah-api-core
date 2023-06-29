package application.sevices

import domain.HealthCheck.{HealthCheck, PostgresStatus}
import cats.effect.*
import cats.effect.implicits.*
import cats.syntax.all.*
import infrastructure.repository.HealthCheckRepository
import org.log4s._

import scala.concurrent.duration.*
trait HealthCheckService {
  def status: IO[HealthCheck]
}

class HealthCheckServiceImpl(healthCheckRepository: HealthCheckRepository) extends HealthCheckService{
  private val logger = org.log4s.getLogger
  def status: IO[HealthCheck] = {
    healthCheckRepository.getPostgresProcessId.flatMap{
      case Some(_) => HealthCheck(PostgresStatus(ok = true)).pure
      case None => HealthCheck(PostgresStatus(ok = false)).pure
    }.handleErrorWith(error => {
      logger.error(s"Postgres response with error => ${error.getMessage}")
      IO(HealthCheck(PostgresStatus(ok = false)))
    })
  }
}