package http.routes

import application.sevices.UserService
import cats.effect.IO
import domain.json.UserJson.*
import domain.models.AppUser.{AppUserCreate, AppUserViewModel}
import io.circe.*
import io.circe.generic.semiauto.*
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
        .as[AppUserCreate]
        .flatMap(user =>
          userService.createUser(user).flatMap {
            case id: String => Ok(id)
            case _          => InternalServerError()
          }
        )
  }
  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}
