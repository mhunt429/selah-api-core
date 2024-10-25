package application.sevices
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits.*
import core.models.AppUser.{AppUserCreateRequest, AppUserViewModel}
import core.validation.{ValidationError, ValidationErrors}
import infrastructure.repository.AppUserRepository
import org.slf4j.LoggerFactory

import java.time.Instant
import java.util.Base64

trait UserService {
  def getUser(id: String): IO[Option[AppUserViewModel]]
  def createUser(
      user: AppUserCreateRequest
  ): IO[Either[List[String], AppUserViewModel]]

  def getUserByEmail(email: String): IO[Option[String]]
}
class UserServiceImpl(
    appUserRepository: AppUserRepository,
    cryptoService: CryptoService
) extends UserService {
  private val logger = LoggerFactory.getLogger(getClass)
  def createUser(
      user: AppUserCreateRequest
  ): IO[Either[List[String], AppUserViewModel]] = {
    validateUser(user) match {
      case Valid(u) =>
        appUserRepository
          .createUser(user)
          .map(createdId => {
            Right(
              AppUserViewModel(
                id = cryptoService.encodeHashId(createdId),
                accountId = cryptoService.encodeHashId(createdId),
                username = "",
                email = user.email,
                firstName = user.firstName,
                phone = user.phone,
                lastName = user.lastName,
                dateCreated = Instant.now()
              )
            )
          })
      case Invalid(errors) =>
        IO.pure(Left(errors.toList.map(_.message)))
    }
  }

  def getUser(id: String): IO[Option[AppUserViewModel]] = {
    val decodedId: Long = cryptoService.decodeHashId(id)
    appUserRepository.getUser(decodedId).map {
      case Some(u) =>
        val fullName = cryptoService.decrypt(u.encryptedName)
        val splitName = fullName.split("|")
        Some(
          AppUserViewModel(
            id = id,
            accountId = cryptoService.encodeHashId(u.accountId),
            email = cryptoService.decrypt(u.encryptedEmail),
            username = u.username,
            firstName = splitName.head,
            lastName = splitName.last,
            phone = cryptoService.decrypt(u.encryptedPhone),
            dateCreated = u.createdDate
          )
        )
      case _ => None
    }
  }

  def getUserByEmail(email: String): IO[Option[String]] = {
    val encryptedEmail: Array[Byte] = cryptoService.encrypt(email)
    val encodedEmail = Base64.getEncoder.encodeToString(encryptedEmail)
    appUserRepository.getUserByEmail(encodedEmail).handleErrorWith { e =>
      IO.pure(None) // Return None or handle it as needed
    }
  }

  private def validateUser(
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
