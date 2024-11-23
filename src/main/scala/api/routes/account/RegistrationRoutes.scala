package api.routes.account

import application.services.account.RegistrationService
import cats.effect.IO
import core.json.AccountJson.*
import core.json.BaseJson.*
import core.models.Account.AccountCreateResponse
import core.models.Http.HttpResponse
import core.models.Registration.RegistrationHttpRequest
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import utils.HttpHelpers

final case class RegistrationRoutes(
    registrationService: RegistrationService
) extends Http4sDsl[IO] {
  private[routes] val prefixPath = "/account/register"
  private val registrationRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root =>
      req.decode[RegistrationHttpRequest] { account =>
        HttpHelpers
          .mapValidationResultToHttpResult[AccountCreateResponse](
            registrationService.registerAccount(account)
          )
          .flatMap {
            case response @ HttpResponse(200, data, _) => Created(response)
            case response                              => BadRequest(response)
          }
      }
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> registrationRoutes
  )
}
