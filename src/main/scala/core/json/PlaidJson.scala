package core.json

import io.circe.*
import io.circe.generic.semiauto.*
import BaseJson.*
import core.Plaid.{PlaidLinkToken, PlaidLinkTokenRequest, PlaidTokenUser}

object PlaidJson {
  implicit val plaidTokenUserDecoder: Decoder[PlaidTokenUser] = deriveDecoder[PlaidTokenUser]
  implicit val plaidTokenUserEncoder: Encoder[PlaidTokenUser] = deriveEncoder[PlaidTokenUser]
  implicit val plaidTokenLinkReqDecoder: Decoder[PlaidLinkTokenRequest] = deriveDecoder[PlaidLinkTokenRequest]
  implicit val plaidTokenLinkReqEncoder: Encoder[PlaidLinkTokenRequest] = deriveEncoder[PlaidLinkTokenRequest]
  implicit val plaidTokenLinkRspDecoder: Decoder[PlaidLinkToken] = deriveDecoder[PlaidLinkToken]
  implicit val plaidTokenLinkRspEncoder: Encoder[PlaidLinkToken] = deriveEncoder[PlaidLinkToken]
}
