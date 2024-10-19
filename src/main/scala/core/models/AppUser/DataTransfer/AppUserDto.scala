package core.models.AppUser.DataTransfer

import io.circe.*
import io.circe.generic.semiauto.*

import java.time.{Instant, ZonedDateTime}



//The view model returns a user but the id is a HashId of the integer value
//This allows use to use auto-incrementing ints on the database for performance but provides security for exposed IDs
case class AppUserViewModel(
                             id: String,
                             accountId: String,
                             email: String,
                             username: String,
                             firstName: String,
                             phone: String,
                             lastName: String,
                             dateCreated: Long
                           )

case class AppUserCreate(
                          email: String,
                          password: String,
                          passwordConfirmation: String,
                          phone: String,
                          firstName: String,
                          lastName: String
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
