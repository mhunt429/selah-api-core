package core.models.AppUser

import io.circe.*
import io.circe.generic.semiauto.*

import java.time.Instant

//The view model returns a user but the id is a HashId of the integer value
//This allows use to use auto-incrementing ints on the database for performance but provides security for exposed IDs
case class AppUserViewModel(
    id: String,
    accountId: String,
    email: String,
    username: String,
    firstName: String,
    lastName: String,
    phone: String,
    dateCreated: Instant
)

case class AppUserCreateRequest(
    email: String,
    password: String,
    passwordConfirmation: String,
    phone: String,
    firstName: String,
    lastName: String,
    accountId: Long,
    dateCreated: Instant = Instant.now()
)

//The below class should be used
case class AppUserUpdateDto(
    email: String,
    phone: String,
    firstName: String,
    lastName: String
) {
  def merge(originalUser: AppUserViewModel): AppUserViewModel = {
    originalUser.copy(
      email = email,
      firstName = firstName,
      lastName = lastName,
      phone = phone
    )
  }
}
