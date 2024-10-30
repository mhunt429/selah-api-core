package application.services

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits.*
import core.models.AppUser.{AppUserCreateRequest, AppUserViewModel}
import core.validation.{ValidationError, ValidationErrors}
import infrastructure.repository.AppUserRepository
object AccountValidationService {
  
  
  //Validator for registration or adding a new user to an existing account
  def validateUser(
      userCreate: AppUserCreateRequest
  ): ValidatedNel[ValidationError, AppUserCreateRequest] = {
    (
      validateEmail(userCreate.email).toValidatedNel,
      validateFirstName(userCreate.firstName).toValidatedNel,
      validateLastName(userCreate.lastName).toValidatedNel,
      validatePassword(userCreate.password).toValidatedNel,
      validatePasswordAndConfirmation(
        userCreate.password,
        userCreate.passwordConfirmation
      ).toValidatedNel
    ).mapN { (_, _, _, _, _) => userCreate }
  }

  private def validateEmail(
      email: String
  ): Validated[ValidationError, String] =
    Either
      .cond(
        email.matches("""^(\w+)@(\w+(.\w+)+)$"""),
        email,
        ValidationErrors.InvalidEmail
      )
      .toValidated

  private def validateFirstName(
      firstName: String
  ): Validated[ValidationError, String] =
    Option
      .when(firstName.trim.nonEmpty)(firstName)
      .toValid(ValidationErrors.FirstNameNotEmpty)

  private def validateLastName(
      lastName: String
  ): Validated[ValidationError, String] =
    Option
      .when(lastName.trim.nonEmpty)(lastName)
      .toValid(ValidationErrors.LastNameNotEmpty)

  private def validatePassword(
      password: String
  ): Validated[ValidationError, String] =
    Option
      .when(password.trim.nonEmpty)(password)
      .toValid(ValidationErrors.PasswordNotEmpty)

  private def validatePasswordAndConfirmation(
      password: String,
      passwordConfirmation: String
  ): Validated[ValidationError, Unit] =
    if (password == passwordConfirmation)
      ().valid
    else
      ValidationErrors.PasswordAndConfirmationMismatch.invalid

}
