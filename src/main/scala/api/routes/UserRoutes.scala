package api.routes

import application.services.account.UserService
import cats.effect.IO
import core.json.UserJson.*
import core.models.AppUser.AppUserCreateRequest
import core.models.Application.AppRequestContext
import io.circe.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}

final case class UserRoutes(
    userService: UserService
) extends Http4sDsl[IO] {

  private[routes] val prefixPath = "/users"

  private val userRoutes: AuthedRoutes[AppRequestContext, IO] =
    AuthedRoutes.of {
      case GET -> Root / id as appRequestContext =>
        userService.getUser(id).flatMap {
          case Some(userData) => Ok(userData)
          case None           => NotFound()
        }

      case req @ POST -> Root as appRequestContext =>
        req.req.decode[AppUserCreateRequest] { newUser =>
          userService.getUserByEmail(newUser.email).flatMap {
            case Some(_) => BadRequest() // Email already exists
            case None =>
              userService.createUser(newUser)(appRequestContext).flatMap {
                case Right(id)              => Ok(id)
                case Left(validationErrors) => BadRequest(validationErrors)
              }
          }
        }
    }

  def routes(
      authMiddleware: AuthMiddleware[IO, AppRequestContext]
  ): HttpRoutes[IO] = {
    Router(
      prefixPath -> authMiddleware(userRoutes)
    )
  }
}
