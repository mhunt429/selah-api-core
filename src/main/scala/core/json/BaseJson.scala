package core.json

import core.models.Http.{HttpErrors, HttpResponse}
import io.circe.*
import io.circe.generic.semiauto.*

import java.text.SimpleDateFormat
import java.util.Date
object BaseJson {

  implicit def httpResponseEncoder[A: Encoder]: Encoder[HttpResponse[A]] =
    deriveEncoder

  implicit def httpResponseDecoder[A: Decoder]: Decoder[HttpResponse[A]] =
    deriveDecoder

  implicit val httpErrorsEncoder: Encoder[HttpErrors] = deriveEncoder
  implicit val httpErrorsDecoder: Decoder[HttpErrors] = deriveDecoder

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"))

  // Encoder for java.util.Date to ISO-8601 string
  implicit val dateEncoder: Encoder[Date] =
    Encoder.encodeString.contramap[Date] { date =>
      dateFormat.format(date)
    }

  // Decoder for ISO-8601 string to java.util.Date
  implicit val dateDecoder: Decoder[Date] = Decoder.decodeString.emap { str =>
    try {
      Right(dateFormat.parse(str))
    } catch {
      case e: Exception => Left(s"Failed to parse date: $str")
    }
  }
}
