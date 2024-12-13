package api.routes.finanalConnector

import application.services.financialConnector.PlaidHttpService
import cats.effect.IO
import core.json.PlaidJson.*
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class FinancialConnectorRoutes(plaidHttpService: PlaidHttpService)
    extends Http4sDsl[IO] {
  private[routes] val prefixPath = "/financialConnector"

  private val financialConnectorRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "link" => {
      plaidHttpService.createLinkToken("123").flatMap {
        case Some(t) => Ok(t.link_token)
        case _       => BadRequest()
      }
    }
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> financialConnectorRoutes
  )
}
