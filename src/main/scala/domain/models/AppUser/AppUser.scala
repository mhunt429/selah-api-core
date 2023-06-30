package domain.models.AppUser

import io.circe.*
import io.circe.generic.semiauto.*

import java.time.{Instant, ZonedDateTime}

object JsonCodecs { // Encoder and Decoder for AppUser
  implicit val appUserEncoder: Encoder[AppUser] = deriveEncoder[AppUser]
  implicit val appUserDecoder: Decoder[AppUser] = deriveDecoder[AppUser]

  // Encoder and Decoder for AppUserViewModel
  implicit val appUserViewModelEncoder: Encoder[AppUserViewModel] =
    deriveEncoder[AppUserViewModel]
  implicit val appUserViewModelDecoder: Decoder[AppUserViewModel] =
    deriveDecoder[AppUserViewModel]

  // Encoder and Decoder for AppUserCreate
  implicit val appUserCreateEncoder: Encoder[AppUserCreate] =
    deriveEncoder[AppUserCreate]
  implicit val appUserCreateDecoder: Decoder[AppUserCreate] =
    deriveDecoder[AppUserCreate]

  // Encoder and Decoder for AppUserUpdate
  implicit val appUserUpdateEncoder: Encoder[AppUserUpdate] =
    deriveEncoder[AppUserUpdate]
  implicit val appUserUpdateDecoder: Decoder[AppUserUpdate] =
    deriveDecoder[AppUserUpdate]
}
case class AppUser(
    id: Long = 0,
    email: String,
    phoneNumber: Option[String],
    firstName: String,
    lastName: String,
    dateCreated: ZonedDateTime,
    dateCreatedUtc: Instant
)

//The view model returns a user but the id is a HashId of the integer value
//This allows use to use auto-incrementing ints on the database for performance but provides security for exposed IDs
case class AppUserViewModel(
    id: String,
    email: String,
    phoneNumber: Option[String],
    firstName: String,
    lastName: String,
    dateCreated: ZonedDateTime,
    dateCreatedUtc: Instant
)

case class AppUserCreate(
    email: String,
    password: String,
    passwordConfirmation: String,
    phoneNumber: Option[String],
    firstName: String,
    lastName: String,
    dateCreated: ZonedDateTime = ZonedDateTime.now(),
    dateCreatedUtc: Instant = Instant.now()
)

case class AppUserUpdate(
    email: String,
    phoneNumber: Option[String],
    firstName: String,
    lastName: String
) {
  def merge(originalUser: AppUser): AppUser = {
    originalUser.copy(
      email = email,
      phoneNumber = phoneNumber,
      firstName = firstName,
      lastName = lastName
    )
  }
}
