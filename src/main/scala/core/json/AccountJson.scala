package core.json

import core.models.Account.AccountCreateResponse
import core.models.Registration.RegistrationHttpRequest
import io.circe.*
import io.circe.generic.semiauto.*
import BaseJson._
object AccountJson {

  implicit val accountCreateEncoder: Encoder[RegistrationHttpRequest] =
    deriveEncoder[RegistrationHttpRequest]
  implicit val accountCreateDecoder: Decoder[RegistrationHttpRequest] =
    deriveDecoder[RegistrationHttpRequest]

  implicit val accountCreateResponseEncoder: Encoder[AccountCreateResponse] =
    deriveEncoder[AccountCreateResponse]
  implicit val accountCreateResponseDecoder: Decoder[AccountCreateResponse] =
    deriveDecoder[AccountCreateResponse]

}
