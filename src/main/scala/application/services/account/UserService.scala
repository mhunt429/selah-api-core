package application.services.account


import application.services.AccountValidationService
import application.services.security.CryptoService
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits.*
import core.models.AppUser.{AppUserCreateRequest, AppUserInsert, AppUserViewModel}
import core.models.Application.AppRequestContext
import core.validation.{ValidationError, ValidationErrors}
import infrastructure.repository.AppUserRepository
import org.slf4j.LoggerFactory
import utils.StringUtilities

import java.time.Instant
import java.util.Base64

trait UserService {
  def getUser(id: String): IO[Option[AppUserViewModel]]
  def createUser(
      user: AppUserCreateRequest
  )(implicit appRequestContext: AppRequestContext): IO[Either[List[String], AppUserViewModel]]

  def getUserByEmail(email: String): IO[Option[String]]
}
class UserServiceImpl(
    appUserRepository: AppUserRepository,
    cryptoService: CryptoService
) extends UserService {
  private val logger = LoggerFactory.getLogger(getClass)
  def createUser(
      user: AppUserCreateRequest
  )(implicit appRequestContext: AppRequestContext): IO[Either[List[String], AppUserViewModel]] = {
    AccountValidationService.validateUser(user) match {
      case Valid(u) =>
        appUserRepository
          .createUserWithCommit(mapCreateUserRequestToDbInsert(user))
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
        val fullName = cryptoService.decrypt(StringUtilities.convertBase64ToBytes(u.encryptedName))
        val splitName = fullName.split(":")
        Some(
          AppUserViewModel(
            id = id,
            accountId = cryptoService.encodeHashId(u.accountId),
            email = cryptoService.decrypt(StringUtilities.convertBase64ToBytes(u.encryptedEmail)),
            username = u.username,
            firstName = splitName.head,
            lastName = splitName.last,
            phone = cryptoService.decrypt(StringUtilities.convertBase64ToBytes(u.encryptedPhone)),
            dateCreated = u.createdDate
          )
        )
      case _ => None
    }
  }

  def getUserByEmail(email: String): IO[Option[String]] = {
    val encryptedEmail: Array[Byte] = cryptoService.encrypt(email)
    val encodedEmail = StringUtilities.convertBytesToBase64(encryptedEmail)
    appUserRepository.getUserByEmail(encodedEmail).handleErrorWith { e =>
      IO.pure(None) 
    }
  }
  
  private def mapCreateUserRequestToDbInsert( user: AppUserCreateRequest)(implicit appRequestContext: AppRequestContext): AppUserInsert = {
    AppUserInsert(
      appLastChangedBy = cryptoService.decodeHashId(appRequestContext.id),
      accountId = cryptoService.decodeHashId(user.accountId),
      encryptedEmail = StringUtilities.convertBytesToBase64(cryptoService.encrypt(user.email)),
      username = user.username,
      password = cryptoService.hashPassword(user.password),
      encryptedName =
        StringUtilities.convertBytesToBase64(cryptoService.encrypt(s"${user.firstName}:${user.lastName}")),
      encryptedPhone = StringUtilities.convertBytesToBase64(cryptoService.encrypt(user.phone)),
      lastLoginIp = appRequestContext.ipAddress,
      phoneVerified = Some(false),
      emailVerified = Some(false)
    )
  }

}
