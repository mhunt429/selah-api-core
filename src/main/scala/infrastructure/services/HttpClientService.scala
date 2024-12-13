package infrastructure.services

import io.circe.*
import io.circe.syntax.*
import sttp.client3.*
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend
import sttp.client3.circe.*

import scala.concurrent.Future

object HttpClientService {
  private val backend = AsyncHttpClientFutureBackend()
  import concurrent.ExecutionContext.Implicits.global

  def getAsync[T: Decoder](
      url: String,
      headers: Map[String, String] = Map.empty
  ): Future[Either[String, T]] = {
    val request = basicRequest
      .get(uri"$url")
      .headers(headers)
      .response(asJson[T])

    backend.send(request).map(processResponse[T])
  }

  def postAsync[T: Decoder, U: Encoder](
      url: String,
      body: U,
      headers: Map[String, String] = Map.empty
  ): Future[Either[String, T]] = {
    val request = basicRequest
      .post(uri"$url")
      .headers(headers)
      .body(body.asJson.noSpaces)
      .contentType("application/json")
      .response(asJson[T])

    backend.send(request).map(processResponse[T])
  }

  def putAsync[T: Decoder, U: Encoder](
      url: String,
      body: U,
      headers: Map[String, String] = Map.empty
  ): Future[Either[String, T]] = {
    val request = basicRequest
      .put(uri"$url")
      .headers(headers)
      .body(body.asJson.noSpaces)
      .contentType("application/json")
      .response(asJson[T])

    backend.send(request).map(processResponse[T])
  }

  def deleteAsync[T: Decoder](
      url: String,
      headers: Map[String, String] = Map.empty
  ): Future[Either[String, T]] = {
    val request = basicRequest
      .delete(uri"$url")
      .headers(headers)
      .response(asJson[T])

    backend.send(request).map(processResponse[T])
  }

  private def processResponse[T: Decoder](
      response: Response[Either[ResponseException[String, Error], T]]
  ): Either[String, T] = {
    response.body match {
      case Right(data) => Right(data)
      case Left(error) =>
        error match {
          case HttpError(body, _) => Left(s"HTTP Error: $body")
          case DeserializationException(original, _) =>
            Left(s"Deserialization Error: $original")
        }
    }
  }
}
