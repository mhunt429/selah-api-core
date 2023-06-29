package modules

import cats.effect.{Async, IO}
import cats.syntax.all.*
import http.routes.HealthCheckRoutes
import modules.Services
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.middleware.*

import scala.concurrent.duration.*
object HttpApi {
  def make[F[_] : Async](services: Services): HttpApi[F] =
    new HttpApi[F](services) {}
}

sealed abstract class HttpApi[F[_]: Async] private (
  services: Services
 ) {
  private val healthRoutes   = HealthCheckRoutes(services.healthCheckService).routes

  private val publicRoutes: HttpRoutes[IO] =
    healthRoutes
private val routes: HttpRoutes[IO] = Router(
  "api/v1" -> publicRoutes
)
  private val middleware: HttpRoutes[IO] => HttpRoutes[IO] = {
    { (http: HttpRoutes[IO]) =>
      AutoSlash(http)
    } andThen { (http: HttpRoutes[IO]) =>
      CORS(http)
    } andThen { (http: HttpRoutes[IO]) =>
      Timeout(60.seconds)(http)
    }
  }
  private val loggers: HttpApp[IO] => HttpApp[IO] = {
    { (http: HttpApp[IO]) =>
      RequestLogger.httpApp(true, false)(http)
    } andThen { (http: HttpApp[IO]) =>( ResponseLogger.httpApp(true, false)(http))
     
    }
  }
  val httpApp: HttpApp[IO] = loggers(middleware(routes).orNotFound)
}