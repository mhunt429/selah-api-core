package core.json

import cats.data.NonEmptyList
import core.models.Http.{HttpErrors, HttpResponse}
import core.transactions.RecurringTransactionFrequency
import core.validation.{ValidationError, ValidationErrors}
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

  implicit val recurringTransactionFrequencyEncoder
      : Encoder[RecurringTransactionFrequency] =
    Encoder[String].contramap[RecurringTransactionFrequency] {
      case RecurringTransactionFrequency.OneTime  => "OneTime"
      case RecurringTransactionFrequency.Weekly   => "Weekly"
      case RecurringTransactionFrequency.BiWeekly => "BiWeekly"
      case RecurringTransactionFrequency.OneTime  => "OneTime"
      case RecurringTransactionFrequency.Monthly  => "Monthly"
      case RecurringTransactionFrequency.Other    => "Other"
    }

  implicit val recurringTransactionFrequencyDecoder
      : Decoder[RecurringTransactionFrequency] =
    Decoder[String].emap {
      case "OneTime" =>
        Right(
          RecurringTransactionFrequency.OneTime
        )
      case "Weekly"   => Right(RecurringTransactionFrequency.Weekly)
      case "BiWeekly" => Right(RecurringTransactionFrequency.BiWeekly)
      case "Monthly"  => Right(RecurringTransactionFrequency.Monthly)
      case "Other"    => Right(RecurringTransactionFrequency.Other)
    }
}
