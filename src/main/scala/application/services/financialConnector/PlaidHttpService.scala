package application.services.financialConnector

import cats.effect.IO
import core.Plaid.{PlaidLinkToken, PlaidLinkTokenRequest, PlaidLinkTokenResponse, PlaidTokenUser}
import core.config.PlaidConfig
import core.json.PlaidJson.*
import infrastructure.services.HttpClientService

import scala.concurrent.ExecutionContext

class PlaidHttpService(config: PlaidConfig)(implicit ec: ExecutionContext) {
  def createLinkToken(userId: String): IO[Option[PlaidLinkTokenResponse]] = {
    val linkTokenRequest = PlaidLinkTokenRequest(
      client_id = config.plaidClientId,
      secret = config.plaidClientSecret,
      user = PlaidTokenUser(client_user_id = userId)
    )

    val url = s"${config.plaidBaseUrl}/link/token/create"
    IO.fromFuture(
      IO(
        HttpClientService.postAsync[PlaidLinkToken, PlaidLinkTokenRequest](
          url,
          linkTokenRequest,
          headers = Map("Content-Type" -> "application/json")
        )
      )
    ).map {
      case Right(t) => Some(PlaidLinkTokenResponse(t.link_token))
      case Left(e)  =>
        // Log the error here
        println(s"Error creating link token: ${e}")
        None
    }
  }
}
