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
trait HealthCheckService {
  def status: IO[HealthCheck]
}

class HealthCheckServiceImpl(healthCheckRepository: HealthCheckRepository) extends HealthCheckService{
  private val log = Slf4jLogger.getLogger[IO]
  def status: IO[HealthCheck] = {
    healthCheckRepository.getPostgresProcessId.flatMap{
      case Some(_) => HealthCheck(PostgresStatus(ok = true)).pure
      case None => HealthCheck(PostgresStatus(ok = false)).pure
    }.handleErrorWith(error => {
      log.error(s"Postgres response with error => $error")
      IO(HealthCheck(PostgresStatus(ok = false)))
    })
  }
}