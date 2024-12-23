package api.routes.identity

import application.services.identity.IdentityService
import cats.effect.IO
import core.identity.LoginRequest
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{HttpRoutes, ResponseCookie}
import utils.Protocol

final case class PublicIdentityRoutes(identityService: IdentityService)
    extends Http4sDsl[IO]
    with Protocol {

  private[routes] val prefixPath = "/identity"

  private val identityRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "login" =>
      req.decode[LoginRequest] { loginRequest =>
        identityService.logUserIn(loginRequest).flatMap {
          case Some(userWithToken) => {
            val jwtToken = userWithToken.tokenData.accessToken
            val cookie = ResponseCookie(
              name = "x_token",
              content = jwtToken,
              path = Some("/"),
              httpOnly = true,
              secure = true
            )
            Ok(userWithToken).map(_.addCookie(cookie))
          }
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
