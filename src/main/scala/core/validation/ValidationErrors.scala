package core.validation

object ValidationErrors {

  final val userNameNotEmpty = "Username cannot be empty"
  final val firstNameNotEmpty = "First Name cannot be empty"
  final val lastNameNotEmpty = "Last Name cannot be empty"
  final val passwordNotEmpty = "Password cannot be empty"
  final val passwordAndConfirmationMismatch =
    "Password and Password Confirmation do not match"
  final val invalidEmail = "Email address is not valid"
}
