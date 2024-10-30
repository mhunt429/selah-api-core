package core.json

import cats.data.NonEmptyList
import core.models.Http.{HttpErrors, HttpResponse}
import core.validation.{ValidationError, ValidationErrors}
import io.circe.*
import io.circe.generic.semiauto.*
object BaseHttpJson {
  implicit def httpResponseEncoder[A: Encoder]: Encoder[HttpResponse[A]] = deriveEncoder

  implicit def httpResponseDecoder[A: Decoder]: Decoder[HttpResponse[A]] = deriveDecoder

  implicit val httpErrorsEncoder: Encoder[HttpErrors] = deriveEncoder
  implicit val httpErrorsDecoder: Decoder[HttpErrors] = deriveDecoder
}
