package application

import application.sevices.HealthCheckServiceImpl
import cats.effect.*
import cats.effect.unsafe.IORuntime
import cats.syntax.all.*
import domain.HealthCheck.{HealthCheck, PostgreSQL}
import infrastructure.repository.HealthCheckRepository
import org.log4s.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
class HealthCheckServiceSpec extends AnyFlatSpec with Matchers {
  private implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global
  private val mockHealthCheckRepository = new HealthCheckRepository {
    override def getPostgresProcessId: IO[Option[String]] =
      IO.pure(Some("processId"))
  }

  "HealthCheckService" should "return HealthCheck with ok=true when Postgres process ID is available" in {
    val healthCheckService =
      new HealthCheckServiceImpl(mockHealthCheckRepository)
    val result = healthCheckService.status.unsafeRunSync()
    result shouldBe HealthCheck(PostgreSQL(ok = true))
  }

  it should "return HealthCheck with ok=false when Postgres process ID is not available" in {
    val mockHealthCheckRepositoryWithoutProcessId = new HealthCheckRepository {
      override def getPostgresProcessId: IO[Option[String]] = IO.pure(None)
    }

    val healthCheckService =
      new HealthCheckServiceImpl(mockHealthCheckRepositoryWithoutProcessId)

    val result = healthCheckService.status.unsafeRunSync()

    result shouldBe HealthCheck(PostgreSQL(ok = false))
  }

  it should "return HealthCheck with ok=false and log the error when an exception occurs" in {
    val mockHealthCheckRepositoryWithError = new HealthCheckRepository {
      override def getPostgresProcessId: IO[Option[String]] =
        IO.raiseError(new RuntimeException("Some error"))
    }

    val healthCheckService =
      new HealthCheckServiceImpl(mockHealthCheckRepositoryWithError)

    val result = healthCheckService.status.unsafeRunSync()

    result shouldBe HealthCheck(PostgreSQL(ok = false))
  }
}
