package application.services.connector

import application.services.security.CryptoService
import cats.effect.IO
import core.Plaid.{PlaidLinkTokenResponse, TokenExchangeHttpRequest}
import core.models.AccountConnector.sql.AccountConnectorSqlInsert
import core.models.Application.AppRequestContext
import infrastructure.repository.AccountConnectorRepository

class ConnectorService(
    plaidHttpService: PlaidHttpService,
    cryptoService: CryptoService,
    accountConnectorRepository: AccountConnectorRepository
) {

  def createLinkToken()(implicit
      appRequestContext: AppRequestContext
  ): IO[Option[PlaidLinkTokenResponse]] = {
    plaidHttpService.createLinkToken(appRequestContext.userId)
  }

  def exchangeLinkToken(tokenExchange: TokenExchangeHttpRequest)(implicit
      appRequestContext: AppRequestContext
  ): IO[Unit] = {
    for {
      accessToken <- plaidHttpService.exchangeLinkToken(
        tokenExchange.publicToken
      )
      modelToSave = mapTokenExchangeToAccountConnectorDb(
        tokenExchange,
        accessToken,
        appRequestContext.userId
      )
      _ <- accountConnectorRepository.insertAccountConnector(modelToSave)
    } yield ()
  }

  private def mapTokenExchangeToAccountConnectorDb(
      tokenExchangeHttpRequest: TokenExchangeHttpRequest,
      accessToken: String,
      appLastChangedById: String
  ): AccountConnectorSqlInsert = {
    AccountConnectorSqlInsert(
      appLastChangedBy = cryptoService.decodeHashId(appLastChangedById),
      userId = cryptoService.decodeHashId(tokenExchangeHttpRequest.userId),
      institutionId = tokenExchangeHttpRequest.institutionId,
      institutionName = tokenExchangeHttpRequest.institutionName,
      encryptedAccessToken = cryptoService.encryptToBase64(accessToken)
    )
  }
}
