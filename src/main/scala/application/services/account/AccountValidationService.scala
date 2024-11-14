package application.services.account

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits.*
import core.models.AppUser.{AppUserCreateRequest, AppUserViewModel}
import core.models.Registration.RegistrationHttpRequest
import core.validation.{ValidationError, ValidationErrors}
import infrastructure.repository.AppUserRepository


object AccountValidationService {
  
  
  //Validator for adding a new user to an existing account
  def validateUser(
      userCreate: AppUserCreateRequest
  ): ValidatedNel[ValidationError, AppUserCreateRequest] = {
    (
      validateEmail(userCreate.email).toValidatedNel,
      validateFirstName(userCreate.firstName).toValidatedNel,
      validateLastName(userCreate.lastName).toValidatedNel,
      validateAccountId(userCreate.accountId).toValidatedNel
    ).mapN { (_, _, _, _) => userCreate }
  }
  
  def validateAccount(createAccountRequest: RegistrationHttpRequest): ValidatedNel[ValidationError, RegistrationHttpRequest] = {
    (
      validateEmail(createAccountRequest.email).toValidatedNel,
      validateFirstName(createAccountRequest.firstName).toValidatedNel,
      validateLastName(createAccountRequest.lastName).toValidatedNel,
      validatePassword(createAccountRequest.password).toValidatedNel,
      validatePasswordAndConfirmation(
        createAccountRequest.password,
        createAccountRequest.passwordConfirmation
      ).toValidatedNel,
    ).mapN { (_, _, _, _, _) => createAccountRequest }
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

  private def validateAccountId(accountId: String): Validated[ValidationError, String] =
    Option.when(accountId.trim.nonEmpty)(accountId)
      .toValid(ValidationErrors.AccountIdEmpty)
}
