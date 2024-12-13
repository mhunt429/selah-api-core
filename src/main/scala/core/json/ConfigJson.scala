package core.json

import core.config.*
import io.circe.*
import io.circe.generic.semiauto.*

object ConfigJson {

  implicit val serverEncoder: Encoder[ServerConfig] =
    deriveEncoder[ServerConfig]
  implicit val serverDecoder: Decoder[ServerConfig] =
    deriveDecoder[ServerConfig]
  implicit val dataBaseEncoder: Encoder[DatabaseConfig] =
    deriveEncoder[DatabaseConfig]
  implicit val databaseDecoder: Decoder[DatabaseConfig] =
    deriveDecoder[DatabaseConfig]
  implicit val plaidEncoder: Encoder[PlaidConfig] = deriveEncoder[PlaidConfig]
  implicit val plaidDecoder: Decoder[PlaidConfig] = deriveDecoder[PlaidConfig]

  implicit val securityEncoder: Encoder[SecurityConfig] =
    deriveEncoder[SecurityConfig]
  implicit val securityDecoder: Decoder[SecurityConfig] =
    deriveDecoder[SecurityConfig]

  implicit val encoder: Encoder[Config] = deriveEncoder[Config]
  implicit val decoder: Decoder[Config] = deriveDecoder[Config]
}
