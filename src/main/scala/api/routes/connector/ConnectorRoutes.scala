package api.routes.connector

import application.services.connector.PlaidHttpService
import cats.effect.IO
import core.Plaid.PlaidLinkTokenResponse
import core.json.BaseJson.*
import core.json.PlaidJson.*
import org.http4s.HttpRoutes
import org.http4s.circe.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import utils.HttpHelpers

final case class ConnectorRoutes(plaidHttpService: PlaidHttpService)
    extends Http4sDsl[IO] {
  private[routes] val prefixPath = "/connector"

  private val connectorRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
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
    prefixPath -> connectorRoutes
  )
}
