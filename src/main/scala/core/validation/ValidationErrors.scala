package core.validation

sealed trait ValidationError {
  def message: String
}

object ValidationErrors {
  case object InvalidEmail extends ValidationError {
    val message: String = "Email address is not valid"
  }

  case object UserNameNotEmpty extends ValidationError {
    val message: String = "Username cannot be empty"
  }

  case object FirstNameNotEmpty extends ValidationError {
    val message: String = "First Name cannot be empty"
  }

  case object LastNameNotEmpty extends ValidationError {
    val message: String = "Last Name cannot be empty"
  }

  case object PasswordNotEmpty extends ValidationError {
    val message: String = "Password cannot be empty"
  }

  case object PasswordAndConfirmationMismatch extends ValidationError {
    val message: String = "Password and Password Confirmation do not match"
  }
}
