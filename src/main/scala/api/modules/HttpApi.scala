package api.modules

import api.routes.{HealthCheckRoutes, MetricsRoutes, UserRoutes}
import cats.effect.{Async, IO}
import cats.syntax.all.*
import io.prometheus.client.CollectorRegistry
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.middleware.*

import scala.concurrent.duration.*

object HttpApi {
  def make(services: Services): HttpApi[IO] =
    new HttpApi[IO](services) {}
}

sealed abstract class HttpApi[F[_]: Async] private (
    services: Services
) {
  private val healthRoutes = HealthCheckRoutes(
    services.healthCheckService
  ).routes

  private val userRoutes = UserRoutes(services.userService).routes

  private val registry: CollectorRegistry = CollectorRegistry.defaultRegistry
  private val metricsRoutes = MetricsRoutes(registry).routes

  private val publicRoutes: HttpRoutes[IO] =
    healthRoutes <+> userRoutes <+> metricsRoutes

  private val routes: HttpRoutes[IO] = Router(
    "api/" -> publicRoutes,
    "" -> healthRoutes,
    "" -> metricsRoutes
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

  val httpApp: HttpApp[IO] = loggers(middleware(routes).orNotFound)
  
}
