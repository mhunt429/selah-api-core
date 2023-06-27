package application.sevices

import domain.HealthCheck.{HealthCheck, PostgresStatus}
import cats.effect.*
import cats.effect.implicits.*
import cats.syntax.all.*
import infrastructure.repository.HealthCheckRepository
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import scala.concurrent.duration.*
trait HealthCheckService[F[_]] {
  def status: F[HealthCheck]
}

class HealthCheckServiceImpl[F[_]: Async](healthCheckRepository: HealthCheckRepository[F]) extends HealthCheckService[F]{
  private val log = Slf4jLogger.getLogger[F]
  def status: F[HealthCheck] = {
    healthCheckRepository.getPostgresProcessId.flatMap{
      case Some(_) => HealthCheck(PostgresStatus(ok = true)).pure
      case None => HealthCheck(PostgresStatus(ok = false)).pure
    }.handleErrorWith(error => {
      log.error(s"Postgres response with error => $error")
      HealthCheck(PostgresStatus(ok = false)).pure})
  }
}