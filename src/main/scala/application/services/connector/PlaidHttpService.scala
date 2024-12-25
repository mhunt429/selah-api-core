package application.services.connector

import cats.effect.IO
import core.Plaid.*
import core.config.PlaidConfig
import infrastructure.services.HttpClientService
import org.slf4j.LoggerFactory
import utils.Protocol

import scala.concurrent.ExecutionContext

class PlaidHttpService(config: PlaidConfig)(implicit ec: ExecutionContext)
    extends Protocol {
  private val logger = LoggerFactory.getLogger(getClass)

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
      case Left(e) =>
        logger.error(s"Plaid API responded with link token error: ${e}")
        None
    }
  }

  def exchangeLinkToken(publicToken: String): IO[String] = {
    val linkTokenRequest = PlaidTokenExchangeRequest(
      client_id = config.plaidClientId,
      secret = config.plaidClientSecret,
      public_token = publicToken
    )
    val url = s"${config.plaidBaseUrl}/item/public_token/exchange"
    IO.fromFuture(
      IO(
        HttpClientService
          .postAsync[PlaidTokenExchangeResponse, PlaidTokenExchangeRequest](
            url,
            linkTokenRequest,
            headers = Map("Content-Type" -> "application/json")
          )
      )
    ).map {
      case Right(t) => t.access_token
      case Left(e) =>
        logger.error(s"Plaid API responded with token exchange error: ${e}")
        ""
    }
  }
}
