package api.middleware

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeError
import cats.syntax.all.catsSyntaxApplicativeError
import cats.syntax.applicativeError.catsSyntaxApplicativeError
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.server.middleware.Logger
import org.slf4j.LoggerFactory

object ErrorHandlingMiddleware {
  // Define a logger
  private val logger = LoggerFactory.getLogger("Selah-API")

  // Middleware that catches unhandled exceptions, logs the stack trace, and returns a 400 response
  def apply(routes: HttpRoutes[IO]): HttpRoutes[IO] = Kleisli {
    (request: Request[IO]) =>
      routes(request).handleErrorWith { error =>
        logger.error(s"${request.uri} encountered an unhandled exception with error message", error)

        OptionT.liftF(IO.pure(Response[IO](status = BadRequest).withEntity("Bad Request")))
      }
  }
}
