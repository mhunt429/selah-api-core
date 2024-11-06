package api.modules

import api.middleware.{ErrorHandlingMiddleware, JwtMiddleware, MetricsMiddleware}
import api.routes.{HealthCheckRoutes, MetricsRoutes, PrivateUserRoutes, PublicUserRoutes}
import cats.effect.{Async, IO}
import cats.syntax.all.*
import core.config.Config
import core.models.Application.AppRequestContext
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
  private val publicUserRoutes = PublicUserRoutes(services.userService).routes

  val authMiddleware: AuthMiddleware[IO, AppRequestContext] = JwtMiddleware(
    config
  )

  private val publicRoutes: HttpRoutes[IO] =
    healthRoutes <+> metricsRoutes <+> publicUserRoutes

  private val userRoutes: HttpRoutes[IO] =
    PrivateUserRoutes(services.userService).routes(authMiddleware)

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

  private val baseRoutes: HttpRoutes[IO] = routes

  private val wrappedRoutes: HttpRoutes[IO] =
    ErrorHandlingMiddleware(
      MetricsMiddleware(
        baseRoutes
      )
    )

  val httpApp: HttpApp[IO] = loggers(
    wrappedRoutes.orNotFound
  )
}
