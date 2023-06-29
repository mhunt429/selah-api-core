package  http.routes

import application.sevices.HealthCheckService
import cats.effect.IO
import org.http4s.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import domain.json.HealthCheckJson.*
import domain.HealthCheck.{HealthCheck, PostgresStatus}
import io.circe.*
import io.circe.generic.semiauto.*

  final case class HealthCheckRoutes(
                                               healthCheckService: HealthCheckService
                                             ) extends Http4sDsl[IO] {

    private[routes] val prefixPath = "/healthcheck"

    private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
      case GET -> Root =>
        healthCheckService.status.flatMap {
          case healthCheck: HealthCheck => Ok(healthCheck)
          case _ => InternalServerError()
        }
    }

    val routes: HttpRoutes[IO] = Router(
      prefixPath -> httpRoutes
    )
  }
