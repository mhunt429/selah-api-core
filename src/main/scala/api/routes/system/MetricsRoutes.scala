package api.routes.system

import cats.effect.*
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.*
import org.http4s.server.Router

import java.io.StringWriter

// Metrics routes for prometheus
final case class MetricsRoutes(registry: CollectorRegistry)
    extends Http4sDsl[IO] {
  private[routes] val prefixPath = "/metrics"
  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      val writer = new StringWriter()
      TextFormat.write004(writer, registry.metricFamilySamples())
      Ok(writer.toString)
        .map(_.withContentType(headers.`Content-Type`(MediaType.text.plain)))
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}
