package application.sevices
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits.*
import core.models.AppUser.{AppUserCreate, AppUserViewModel}
import core.validation.{ValidationError, ValidationErrors}
import infrastructure.repository.AppUserRepository

import java.time.Instant

trait UserService {
  def getUser(id: String): IO[Option[AppUserViewModel]]
  def createUser(
      user: AppUserCreate
  ): IO[Either[List[String], AppUserViewModel]]

  def getUserByEmail(email: String): IO[Option[String]]
}
class UserServiceImpl(
    appUserRepository: AppUserRepository,
    securityService: SecurityService
) extends UserService {

  def createUser(
      user: AppUserCreate
  ): IO[Either[List[String], AppUserViewModel]] = {
    validateUser(user) match {
      case Valid(u) =>
        appUserRepository
          .createUser(user)
          .map(createdId => {
            Right(
              AppUserViewModel(
                id = securityService.encodeHashId(createdId),
                accountId = securityService.encodeHashId(createdId),
                username = "",
                email = user.email,
                firstName = user.firstName,
                phone = user.phone,
                lastName = user.lastName,
                dateCreated = Instant.now().toEpochMilli
              )
            )
          })
      case Invalid(errors) =>
        IO.pure(Left(errors.toList.map(_.message)))
    }
  }

  def getUser(id: String): IO[Option[AppUserViewModel]] = {
    val decodedId: Long = securityService.decodeHashId(id)
    appUserRepository.getUser(decodedId).map {
      case Some(u) =>
        val fullName = securityService.decrypt(u.encryptedName)
        val splitName = fullName.split("|")
        Some(
          AppUserViewModel(
            id = id,
            accountId = securityService.encodeHashId(u.accountId),
            email = securityService.decrypt(u.encryptedEmail),
            username = u.username,
            firstName = splitName.head,
            lastName = splitName.last,
            phone = securityService.decrypt(u.encryptedPhone),
            dateCreated = u.createdEpoch,
          )
        )
      case _ => None
    }
  }

  def getUserByEmail(email: String): IO[Option[String]] =
    appUserRepository.getUserByEmail(email)

  private def validateUser(
      userCreate: AppUserCreate
  ): ValidatedNel[ValidationError, AppUserCreate] = {
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
