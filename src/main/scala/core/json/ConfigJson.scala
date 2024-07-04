package core.json

import core.config.{Config, ServerConfig}
import io.circe.*
import io.circe.generic.semiauto.*

object ConfigJson {

  implicit val serverEncoder: Encoder[ServerConfig] = deriveEncoder[ServerConfig]
  implicit val serverDecoder: Decoder[ServerConfig] = deriveDecoder[ServerConfig]

  implicit val encoder: Encoder[Config] = deriveEncoder[Config]
  implicit val decoder: Decoder[Config] = deriveDecoder[Config]
}