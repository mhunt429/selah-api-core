package api.routes.finanalConnector

import application.services.financialConnector.PlaidHttpService
import cats.effect.IO
import core.Plaid.PlaidLinkTokenResponse
import core.json.BaseJson.*
import core.json.PlaidJson.*
import core.models.AppUser.AppUserViewModel
import org.http4s.circe.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{EntityEncoder, HttpRoutes}
import utils.HttpHelpers

final case class FinancialConnectorRoutes(plaidHttpService: PlaidHttpService)
    extends Http4sDsl[IO] {
  private[routes] val prefixPath = "/financialConnector"

  private val financialConnectorRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "link" => {
      plaidHttpService.createLinkToken("123").flatMap {
        case Some(tokeResponse) =>
          Ok(
            HttpHelpers.getSuccessResult[PlaidLinkTokenResponse](
              tokeResponse,
              200
            )
          )
        case _ => BadRequest()
      }
    }
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> financialConnectorRoutes
  )
}
