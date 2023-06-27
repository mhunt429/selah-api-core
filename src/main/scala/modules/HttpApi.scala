package modules

import cats.effect.Async
import cats.syntax.all.*
import http.routes.HealthCheckRoutes
import modules.Services
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware._
import scala.concurrent.duration._
object HttpApi {
  def make[F[_] : Async](services: Services[F]): HttpApi[F] =
    new HttpApi[F](services) {}
}

sealed abstract class HttpApi[F[_]: Async] private (
  services: Services[F],
 ) {
  private val healthRoutes   = HealthCheckRoutes[F](services.healthCheckService).routes

  private val publicRoutes: HttpRoutes[F] =
    healthRoutes
private val routes: HttpRoutes[F] = Router(
  "v1" -> publicRoutes
)
  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { (http: HttpRoutes[F]) =>
      AutoSlash(http)
    } andThen { (http: HttpRoutes[F]) =>
      CORS(http)
    } andThen { (http: HttpRoutes[F]) =>
      Timeout(60.seconds)(http)
    }
  }
  private val loggers: HttpApp[F] => HttpApp[F] = {
    { (http: HttpApp[F]) =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { (http: HttpApp[F]) =>( ResponseLogger.httpApp(true, true)(http))
     
    }
  }
  val httpApp: HttpApp[F] = loggers(middleware(routes).orNotFound)
}