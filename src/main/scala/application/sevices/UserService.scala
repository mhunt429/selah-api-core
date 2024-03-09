package application.sevices
import cats.data.*
import cats.data.Validated.*
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.*
import domain.models.AppUser.{AppUserCreate, AppUserViewModel}
import domain.models.validation.ValidationResult
import domain.validation.ValidationErrors
import infrastructure.repository.AppUserRepository
import org.hashids.Hashids

import scala.util.matching.Regex
trait UserService {
  def getUser(id: String): IO[Option[AppUserViewModel]]
  def createUser(user: AppUserCreate): IO[String]

  def getUserByEmail(email: String): IO[Option[String]]
}
class UserServiceImpl(
    appUserRepository: AppUserRepository,
    securityService: SecurityService
) extends UserService {

  def createUser(user: AppUserCreate): IO[String] = {
    for {
      userId <- appUserRepository.createUser(user)
      hashId <- securityService.encodeHashId(userId)
    } yield hashId
  }

  def getUser(id: String): IO[Option[AppUserViewModel]] = {
    val user = for {
      decodedId <- securityService.decodeHashId(id)
      user <- appUserRepository.getUser(decodedId)
    } yield (user)

    user.map {
      case Some(u) =>
        Some(
          AppUserViewModel(
            id = id,
            email = u.email,
            firstName = u.firstName,
            lastName = u.lastName,
            dateCreated = u.dateCreated
          )
        )
      case _ => None
    }
  }

   def getUserByEmail(email: String): IO[Option[String]] = appUserRepository.getUserByEmail(email)

  private def validateUser(userCreate: AppUserCreate): List[String] = {
     List.empty
  }


  private def validateEmail(email: String): Validated[ValidationResult, String] =
    Either
      .cond(email.matches("""^(\w+)@(\w+(.\w+)+)$"""),
        email,
        ValidationResult(ValidationErrors.invalidEmail))
      .toValidated
  }

  private def validateFirstName(firstName: String): Option[String] = {
    firstName.trim match {
      case "" => Some(ValidationErrors.firstNameNotEmpty)
      case _  => None
    }
  }

  private def validateLastName(lastName: String): Option[String] = {
    lastName.trim match {
      case "" => Some(ValidationErrors.lastNameNotEmpty)
      case _  => None
    }
  }

  private def validatePassword(password: String): Option[String] = {
    password.trim match {
      case "" => Some(ValidationErrors.passwordNotEmpty)
      case _  => None
    }
  }

  private def validatePasswordAndConfirmation(
      password: String,
      passwordConfirmation: String
  ): Option[String] = {
    password == passwordConfirmation match {
      case true => None
      case _    => Some(ValidationErrors.passwordAndConfirmationMismatch)
    }
  }

