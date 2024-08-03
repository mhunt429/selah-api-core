package infrastructure.metrics

import io.prometheus.client.{Counter, Summary}

object PrometheusMetrics {
  val requestsCounter: Counter = Counter.build()
    .name("api_requests_total")
    .help("Total number of API requests.")
    .register()

  val requestLatencyMs: Summary = Summary.build()
    .name("http_request_duration_milliseconds")
    .help("Request latency in milliseconds.")
    .register()
}


