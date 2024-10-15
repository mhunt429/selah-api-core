package api.modules

import api.middleware.{JwtMiddleware, MetricsMiddleware}
import api.routes.{HealthCheckRoutes, MetricsRoutes, UserRoutes}
import cats.effect.{Async, IO}
import cats.syntax.all.*
import core.config.Config
import io.prometheus.client.CollectorRegistry
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.middleware.*
import org.http4s.server.{AuthMiddleware, Router}

import scala.concurrent.duration.*

object HttpApi {
  def make(services: Services, config: Config): HttpApi[IO] =
    new HttpApi[IO](services, config) {}
}

sealed abstract class HttpApi[F[_]: Async] private (
    services: Services,
    config: Config
) {
  private val healthRoutes = HealthCheckRoutes(
    services.healthCheckService
  ).routes

  private val registry: CollectorRegistry = CollectorRegistry.defaultRegistry
  private val metricsRoutes = MetricsRoutes(registry).routes

  val authMiddleware: AuthMiddleware[IO, String] = JwtMiddleware(config)

  private val publicRoutes: HttpRoutes[IO] =
    healthRoutes <+> metricsRoutes

  private val userRoutes: HttpRoutes[IO] =
    UserRoutes(services.userService).routes(authMiddleware)

  private val routes: HttpRoutes[IO] = Router(
    "api/" -> (publicRoutes <+> userRoutes),
    "" -> (healthRoutes <+> metricsRoutes)
  )

  private val middleware: HttpRoutes[IO] => HttpRoutes[IO] = {
    { (http: HttpRoutes[IO]) =>
      AutoSlash(http)
    } andThen { (http: HttpRoutes[IO]) =>
      CORS.policy.withAllowOriginAll(http)
    } andThen { (http: HttpRoutes[IO]) =>
      Timeout(60.seconds)(http)
    }
  }
  private val loggers: HttpApp[IO] => HttpApp[IO] = {
    { (http: HttpApp[IO]) =>
      RequestLogger.httpApp(true, false)(http)
    } andThen { (http: HttpApp[IO]) =>
      (ResponseLogger.httpApp(true, false)(http))

    }
  }

  val httpApp: HttpApp[IO] = loggers(
    middleware(MetricsMiddleware(routes)).orNotFound
  )

}
