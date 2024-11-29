package application.services.account

import application.services.security.CryptoService
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}
import cats.effect.IO
import cats.implicits.*
import core.models.AppUser.sql.AppUserInsert
import core.models.AppUser.{AppUserCreateRequest, AppUserViewModel}
import core.models.Application.AppRequestContext
import core.validation.ValidationError
import infrastructure.repository.AppUserRepository
import org.slf4j.LoggerFactory
import utils.StringUtilities

import java.time.Instant

trait UserService {
  def getUser(id: String): IO[Option[AppUserViewModel]]
  def createUser(
      user: AppUserCreateRequest
  )(implicit
      appRequestContext: AppRequestContext
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
  )(implicit
      appRequestContext: AppRequestContext
  ): IO[Either[List[String], AppUserViewModel]] = {
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
        val fullName = cryptoService
          .decrypt(StringUtilities.convertBase64ToBytes(u.encryptedName))
        val splitName = fullName.split(":")
        Some(
          AppUserViewModel(
            id = id,
            accountId = cryptoService.encodeHashId(u.accountId),
            email = cryptoService
              .decrypt(StringUtilities.convertBase64ToBytes(u.encryptedEmail)),
            username = u.username,
            firstName = splitName.head,
            lastName = splitName.last,
            phone = cryptoService
              .decrypt(StringUtilities.convertBase64ToBytes(u.encryptedPhone)),
            dateCreated = u.createdDate
          )
        )
      case _ => None
    }
  }

  def getUserByEmail(email: String): IO[Option[String]] = {
    val encryptedEmail: Array[Byte] = cryptoService.encrypt(email)
    val encodedEmail = StringUtilities.convertBytesToBase64(encryptedEmail)
    appUserRepository.getUserByEmail(encodedEmail)
  }

  private def mapCreateUserRequestToDbInsert(
      user: AppUserCreateRequest
  )(implicit appRequestContext: AppRequestContext): AppUserInsert = {
    val appLastChangedBy = cryptoService.decodeHashId(appRequestContext.userId)
    val accountId = cryptoService.decodeHashId(user.accountId)
    val encryptedEmail =
      cryptoService.encryptToBase64(user.email)
    val password = cryptoService.hashPassword(user.password)
    val encryptedName =
      cryptoService.encryptToBase64(s"${user.firstName}:${user.lastName}")

    val encryptedPhone =
      cryptoService.encryptToBase64(user.phone)

    AppUserInsert(
      appLastChangedBy = appLastChangedBy,
      accountId = cryptoService.decodeHashId(user.accountId),
      encryptedEmail = encryptedEmail,
      username = user.username,
      password = password,
      encryptedName = encryptedName,
      encryptedPhone = encryptedPhone,
      lastLoginIp = appRequestContext.ipAddress,
      phoneVerified = Some(false),
      emailVerified = Some(false)
    )
  }

}
