package api.routes.account

import application.services.account.RegistrationService
import cats.effect.IO
import core.json.AccountJson.*
import core.json.BaseHttpJson.*
import core.models.Account.AccountCreateResponse
import core.models.Http.HttpResponse
import core.models.Registration.RegistrationHttpRequest
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class RegistrationRoutes(
    registrationService: RegistrationService
) extends Http4sDsl[IO] {
  private[routes] val prefixPath = "/account/register"
  private val registrationRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root =>
      req.decode[RegistrationHttpRequest] { account =>
        registrationService.registerAccount(account).flatMap {
          case Right(account) =>
            Ok(
              HttpResponse[AccountCreateResponse](
                statusCode = 200,
                data = Some(account)
              )
            )
          case Left(errors) =>
            BadRequest(
              HttpResponse[AccountCreateResponse](
                statusCode = 400,
                data = None,
                errors = errors
              )
            )
        }
      }
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> registrationRoutes
  )
}
