package api.routes

import application.services.account.UserService
import cats.effect.IO
import core.json.UserJson.*
import core.models.AppUser.AppUserCreateRequest
import core.models.Application.AppRequestContext
import io.circe.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.util.CaseInsensitiveString
import org.http4s.{AuthedRoutes, EntityDecoder, HttpRoutes}

final case class PrivateUserRoutes(
    userService: UserService
) extends Http4sDsl[IO] {
  private[routes] val prefixPath = "/users"
  private val userRoutes: AuthedRoutes[AppRequestContext, IO] = AuthedRoutes.of {
    case GET -> Root / id as appRequestContext : AppRequestContext =>
      userService.getUser(id).flatMap {
        case Some(userData) => Ok(userData)
        case None           => NotFound()
      }
  }
  def routes(authMiddleware: AuthMiddleware[IO, AppRequestContext]): HttpRoutes[IO] = {
    Router(
      prefixPath -> authMiddleware(userRoutes)
    )
  }
}

final case class PublicUserRoutes(
    userService: UserService
) extends Http4sDsl[IO] {
  implicit val decoder: EntityDecoder[IO, AppUserCreateRequest] =
    jsonOf[IO, AppUserCreateRequest]
  private[routes] val prefixPath = "/users"

  private val userRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    // Public POST route for user registration
    case req @ POST -> Root =>
      req.decode[AppUserCreateRequest] { newUser =>
        userService.getUserByEmail(newUser.email).flatMap {
          case Some(_) => BadRequest() // Email already exists
          case None =>
            userService.createUser(newUser).flatMap {
              case Right(id)              => Ok(id) // User created successfully
              case Left(validationErrors) => BadRequest(validationErrors)
            }
        }
      }
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> userRoutes
  )
}
