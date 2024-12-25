package api.routes.connector

import application.services.connector.{ConnectorService, PlaidHttpService}
import cats.effect.IO
import core.Plaid.{PlaidLinkTokenResponse, TokenExchangeHttpRequest}
import core.json.BaseJson.*
import core.models.Application.AppRequestContext
import org.http4s.circe.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes, Response, Status}
import utils.{HttpHelpers, Protocol}

final case class ConnectorRoutes(
    connectorService: ConnectorService
) extends Http4sDsl[IO]
    with Protocol {
  private[routes] val prefixPath = "/connector"

  private val connectorRoutes: AuthedRoutes[AppRequestContext, IO] =
    AuthedRoutes.of {
      case req @ POST -> Root / "link" as appRequestContext =>
        connectorService.createLinkToken()(appRequestContext).flatMap {
          case Some(tokeResponse) =>
            Ok(
              HttpHelpers.getSuccessResult[PlaidLinkTokenResponse](
                tokeResponse,
                200
              )
            )
          case _ => BadRequest()
        }

      case req @ POST -> Root / "exchange" as appRequestContext =>
        req.req.decode[TokenExchangeHttpRequest] { tokenExchange =>
          connectorService
            .exchangeLinkToken(tokenExchange)(appRequestContext)
            .flatMap { _ =>
              Ok()
            }
        }
    }

  def routes(
      authMiddleware: AuthMiddleware[IO, AppRequestContext]
  ): HttpRoutes[IO] = {
    Router(
      prefixPath -> authMiddleware(connectorRoutes)
    )
  }
}
