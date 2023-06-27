package  http.routes

import application.sevices.HealthCheckService
import cats.Monad
import org.http4s.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import domain.json.HealthCheckJson._
import domain.HealthCheck.{HealthCheck, PostgresStatus}
import io.circe.*
import io.circe.generic.semiauto.*

  final case class HealthCheckRoutes[F[_] : Monad](
                                               healthCheckService: HealthCheckService[F]
                                             ) extends Http4sDsl[F] {

    private[routes] val prefixPath = "/healthcheck"

    private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
      case GET -> Root =>
        Ok(healthCheckService.status)
    }

    val routes: HttpRoutes[F] = Router(
      prefixPath -> httpRoutes
    )
  }
