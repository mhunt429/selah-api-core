package domain.json
import domain.HealthCheck.{HealthCheck, PostgresStatus}
import io.circe.*
import io.circe.generic.semiauto.*
object HealthCheckJson {
 implicit val postgresHealthEncoder:Encoder[PostgresStatus] = deriveEncoder[PostgresStatus]
 implicit val postgresHealthDecoder:Decoder[PostgresStatus] = deriveDecoder[PostgresStatus]

 implicit val healthCheckEncoder: Encoder[HealthCheck] = deriveEncoder[HealthCheck]
 implicit val healthCheckDecoder: Decoder[HealthCheck] = deriveDecoder[HealthCheck]
}