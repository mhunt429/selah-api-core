package api.routes.identity

import application.services.account.UserService
import cats.effect.IO
import core.json.BaseJson.*
import core.json.UserJson.*
import core.models.AppUser.AppUserViewModel
import core.models.Application.AppRequestContext
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}
import utils.HttpHelpers

final case class IdentityRoutes(userService: UserService)
    extends Http4sDsl[IO] {
  private[routes] val prefixPath = "/identity"

  private val identityRoutes: AuthedRoutes[AppRequestContext, IO] =
    AuthedRoutes.of { case GET -> Root / "current-user" as appRequestContext =>
      userService.getUser(appRequestContext.id).flatMap {
        case Some(user) =>
          Ok(
            HttpHelpers.getSuccessResult[AppUserViewModel](user, 200)
          )
        case _ => NoContent()
      }
    }

  def routes(
      authMiddleware: AuthMiddleware[IO, AppRequestContext]
  ): HttpRoutes[IO] = {
    Router(
      prefixPath -> authMiddleware(identityRoutes)
    )
  }

}
