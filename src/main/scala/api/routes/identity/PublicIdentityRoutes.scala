package api.routes.identity

import application.services.identity.IdentityService
import cats.data.NonEmptyList
import cats.effect.IO
import core.identity.LoginRequest
import core.json.BaseJson.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`WWW-Authenticate`
import org.http4s.server.Router
import org.http4s.{Challenge, Headers, HttpRoutes}
import utils.Protocol

final case class PublicIdentityRoutes(identityService: IdentityService)
    extends Http4sDsl[IO] with Protocol {

  private[routes] val prefixPath = "/identity"

  private val identityRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "login" =>
      req.decode[LoginRequest] { loginRequest =>
        identityService.logUserIn(loginRequest).flatMap {
          case Some(userWithToken) => Ok(userWithToken)
          case _ =>
            BadRequest(
            )
        }
      }
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> identityRoutes
  )
}
