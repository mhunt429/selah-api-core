package api.middleware

import cats.data.Kleisli
import cats.effect.*
import infrastructure.metrics.PrometheusMetrics
import org.http4s.*
import org.http4s.server.*

object MetricsMiddleware {

  def apply[F[_]: Sync](httpRoutes: HttpRoutes[F]): HttpRoutes[F] = {
    Kleisli { (req: Request[F]) =>
      val timer = PrometheusMetrics.requestLatencyMs.startTimer()
      httpRoutes(req).map { response =>
        timer.observeDuration()
        response
      }
    }
  }
}
