package core.json

import core.models.{HealthCheck, PostgreSQL}
import io.circe.*
import io.circe.generic.semiauto.*
object HealthCheckJson {
  implicit val postgresHealthEncoder: Encoder[PostgreSQL] =
    deriveEncoder[PostgreSQL]
  implicit val postgresHealthDecoder: Decoder[PostgreSQL] =
    deriveDecoder[PostgreSQL]

  implicit val healthCheckEncoder: Encoder[HealthCheck] =
    deriveEncoder[HealthCheck]
  implicit val healthCheckDecoder: Decoder[HealthCheck] =
    deriveDecoder[HealthCheck]
}
