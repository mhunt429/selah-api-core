package core.models.Registration

import java.time.Instant

case class RegistrationHttpRequest(
    accountName: Option[String],
    username: String,
    email: String,
    password: String,
    passwordConfirmation: String,
    phone: String,
    firstName: String,
    lastName: String
)
