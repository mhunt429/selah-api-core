package core.json

import cats.effect.IO
import core.Plaid.{PlaidLinkToken, PlaidLinkTokenRequest, PlaidLinkTokenResponse, PlaidTokenUser}
import io.circe.*
import io.circe.generic.semiauto.*
import org.http4s.EntityEncoder
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder

object PlaidJson {
  implicit val plaidTokenUserDecoder: Decoder[PlaidTokenUser] =
    deriveDecoder[PlaidTokenUser]
  implicit val plaidTokenUserEncoder: Encoder[PlaidTokenUser] =
    deriveEncoder[PlaidTokenUser]
  implicit val plaidTokenLinkReqDecoder: Decoder[PlaidLinkTokenRequest] =
    deriveDecoder[PlaidLinkTokenRequest]
  implicit val plaidTokenLinkReqEncoder: Encoder[PlaidLinkTokenRequest] =
    deriveEncoder[PlaidLinkTokenRequest]
  implicit val plaidTokenLinkRspDecoder: Decoder[PlaidLinkToken] =
    deriveDecoder[PlaidLinkToken]
  implicit val plaidTokenLinkRspEncoder: Encoder[PlaidLinkToken] =
    deriveEncoder[PlaidLinkToken]

  implicit val plaidTokenLinkViewModelDecoder: Decoder[PlaidLinkTokenResponse] =
    deriveDecoder[PlaidLinkTokenResponse]

  implicit val plaidTokenLinkViewModelEncoder: Encoder[PlaidLinkTokenResponse] =
    deriveEncoder[PlaidLinkTokenResponse]

}
