package http

import api.routes.HealthCheckRoutes
import application.sevices.HealthCheckService
import cats.effect.*
import cats.effect.unsafe.IORuntime
import core.models.{HealthCheck, PostgreSQL}
import org.http4s.*
import org.http4s.Method.*
import org.http4s.client.dsl.io.*
import org.http4s.syntax.literals.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HealthCheckHttpSpec extends AnyFlatSpec with Matchers {
  private implicit val runtime: IORuntime = IORuntime.global
  private val mockHealthCheckService = new HealthCheckService {
    override def status: IO[HealthCheck] =
      IO.pure(HealthCheck(PostgreSQL(true)))
  }

  private val healthCheckUri = uri"/healthcheck"

  "HealthCheckRoutes" should "return 200 when connected to PostgreSQL" in {
    val req = GET(healthCheckUri)
    val routes = HealthCheckRoutes(mockHealthCheckService).routes
    val response = routes.run(req)
    response.map(_.status).value.unsafeRunSync().get shouldBe Status.Ok
  }
}
