package api.routes.system

import application.services.HealthCheckService
import cats.effect.IO
import core.json.BaseJson.*
import core.json.HealthCheckJson.*
import core.models.HealthCheck
import org.http4s.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import utils.HttpHelpers

final case class HealthCheckRoutes(
    healthCheckService: HealthCheckService
) extends Http4sDsl[IO] {

  private[routes] val prefixPath = "/healthcheck"
  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      healthCheckService.status.flatMap {
        case healthCheck: HealthCheck =>
          Ok(HttpHelpers.getSuccessResult[HealthCheck](healthCheck, 200))
        case _ => BadGateway()
      }
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}
