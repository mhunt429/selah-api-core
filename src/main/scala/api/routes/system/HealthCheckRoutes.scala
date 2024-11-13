package api.routes.system

import application.services.HealthCheckService
import cats.effect.IO
import core.json.BaseJson.*
import core.json.HealthCheckJson.*
import core.models.Http.HttpResponse
import core.models.{HealthCheck, PostgreSQL}
import io.circe.*
import io.circe.generic.semiauto.*
import org.http4s.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class HealthCheckRoutes(
    healthCheckService: HealthCheckService
) extends Http4sDsl[IO] {

  private[routes] val prefixPath = "/healthcheck"
  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      healthCheckService.status.flatMap {
        case healthCheck: HealthCheck => Ok(HttpResponse[HealthCheck](statusCode = 200, data = Some(healthCheck)))
        case _                        => InternalServerError()
      }
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}