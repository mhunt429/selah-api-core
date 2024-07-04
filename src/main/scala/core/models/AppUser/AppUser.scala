package core.models.AppUser

import io.circe.*
import io.circe.generic.semiauto.*

import java.time.{Instant, ZonedDateTime}

case class AppUser(
    id: Long,
    email: String,
    firstName: String,
    lastName: String,
    dateCreated: Long
)

//The view model returns a user but the id is a HashId of the integer value
//This allows use to use auto-incrementing ints on the database for performance but provides security for exposed IDs
case class AppUserViewModel(
    id: String,
    email: String,
    firstName: String,
    lastName: String,
    dateCreated: Long
)

case class AppUserCreate(
    email: String,
    password: String,
    passwordConfirmation: String,
    phoneNumber: Option[String],
    firstName: String,
    lastName: String
    // dateCreated: ZonedDateTime = ZonedDateTime.now(),
    //dateCreatedUtc: Instant = Instant.now()
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
      firstName = firstName,
      lastName = lastName
    )
  }
}
