package core.json


import core.models.AppUser.*
import core.models.AppUser.sql.{AppUser, AppUserUpdate}
import io.circe.*
import io.circe.generic.semiauto.*

object UserJson {
  implicit val appUserEncoder: Encoder[AppUser] = deriveEncoder[AppUser]
  implicit val appUserDecoder: Decoder[AppUser] = deriveDecoder[AppUser]

  // Encoder and Decoder for AppUserViewModel
  implicit val appUserViewModelEncoder: Encoder[AppUserViewModel] =
    deriveEncoder[AppUserViewModel]
  implicit val appUserViewModelDecoder: Decoder[AppUserViewModel] =
    deriveDecoder[AppUserViewModel]

  // Encoder and Decoder for AppUserCreate
  implicit val appUserCreateEncoder: Encoder[AppUserCreateRequest] =
    deriveEncoder[AppUserCreateRequest]
  implicit val appUserCreateDecoder: Decoder[AppUserCreateRequest] =
    deriveDecoder[AppUserCreateRequest]

  // Encoder and Decoder for AppUserUpdate
  implicit val appUserUpdateEncoder: Encoder[AppUserUpdate] =
    deriveEncoder[AppUserUpdate]
  implicit val appUserUpdateDecoder: Decoder[AppUserUpdate] =
    deriveDecoder[AppUserUpdate]
}
