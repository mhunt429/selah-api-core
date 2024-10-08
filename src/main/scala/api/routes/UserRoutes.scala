package api.routes

import application.sevices.UserService
import cats.effect.IO
import core.json.UserJson.*
import core.models.AppUser.{AppUserCreate, AppUserViewModel}
import io.circe.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{EntityDecoder, HttpRoutes}

final case class UserRoutes(
    userService: UserService
) extends Http4sDsl[IO] {
  implicit val decoder: EntityDecoder[IO, AppUserCreate] =
    jsonOf[IO, AppUserCreate]
  private[routes] val prefixPath = "/users"
  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / id =>
      userService.getUser(id).flatMap {
        case Some(user) => Ok(user)
        case _          => NotFound()
      }

    case req @ POST -> Root =>
      req
        .decode[AppUserCreate] { user =>
          userService.getUserByEmail(user.email).flatMap {
            case Some(_) => BadRequest() //TODO require recaptcha
            case _ =>
              userService.createUser(user).flatMap {
                case Right(id)              => Ok(id)
                case Left(validationErrors) => BadRequest(validationErrors)
              }
          }
        }
  }
  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}
