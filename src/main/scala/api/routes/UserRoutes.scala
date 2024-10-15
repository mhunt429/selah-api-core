package api.routes

import application.sevices.UserService
import cats.effect.IO
import core.json.UserJson.*
import core.models.AppUser.{AppUserCreate, AppUserViewModel}
import io.circe.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, EntityDecoder, HttpRoutes}

final case class UserRoutes(
    userService: UserService
) extends Http4sDsl[IO] {

  implicit val decoder: EntityDecoder[IO, AppUserCreate] =
    jsonOf[IO, AppUserCreate]

  private[routes] val prefixPath = "/users"

  // Update to use AuthedRoutes with the authMiddleware applied
  private val authedRoutes: AuthedRoutes[String, IO] = AuthedRoutes.of {

    // Authenticated GET route
    case GET -> Root / id as user =>
      userService.getUser(id).flatMap {
        case Some(userData) => Ok(userData)
        case None           => NotFound()
      }

    case req @ POST -> Root as user =>
      req.req.decode[AppUserCreate] { newUser =>
        userService.getUserByEmail(newUser.email).flatMap {
          case Some(_) => BadRequest() // Require recaptcha in future
          case None =>
            userService.createUser(newUser).flatMap {
              case Right(id)              => Ok(id)
              case Left(validationErrors) => BadRequest(validationErrors)
            }
        }
      }
  }

  // Combine routes with middleware
  def routes(authMiddleware: AuthMiddleware[IO, String]): HttpRoutes[IO] = {
    Router(prefixPath -> authMiddleware(authedRoutes))
  }
}
