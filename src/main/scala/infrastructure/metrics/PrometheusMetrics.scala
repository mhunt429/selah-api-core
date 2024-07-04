package infrastructure.metrics

import io.prometheus.client.Counter

object PrometheusMetrics {
  val requestsCounter: Counter = Counter.build()
    .name("api_requests_total")
    .help("Total number of API requests.")
    .register()
}