package  api.middleware

import cats.data.Kleisli
import cats.effect.*
import infrastructure.metrics.PrometheusMetrics
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.*
import org.http4s.server.middleware.*



object MetricsMiddleware {

  def apply[F[_]: Sync](httpRoutes: HttpRoutes[F]): HttpRoutes[F] = {
    Kleisli { (req: Request[F]) =>
      PrometheusMetrics.requestsCounter.inc()  // Increment the counter
      httpRoutes(req)
    }
  }
}
