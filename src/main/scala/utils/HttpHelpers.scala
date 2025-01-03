package utils

import cats.effect.IO
import core.models.Http.HttpResponse
import org.http4s.dsl.Http4sDsl

object HttpHelpers extends Http4sDsl[IO] {
  def mapValidationResultToHttpResult[A](
      result: IO[Either[List[String], A]]
  ): IO[HttpResponse[A]] = {
    result.map {
      case Right(result) =>
        HttpResponse[A](
          statusCode = 200,
          data = Some(result)
        )
      case Left(errors) =>
        HttpResponse[A](
          statusCode = 400,
          data = None,
          errors = errors
        )
    }
  }

  //Use for 200, 201s and 204s
  def getSuccessResult[A](data: A, statusCode: Int): HttpResponse[A] =
    HttpResponse[A](
      statusCode = statusCode,
      data = Some(data)
    )
}
