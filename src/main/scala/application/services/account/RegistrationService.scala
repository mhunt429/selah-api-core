package application.services.account

import application.services.AccountValidationService
import application.services.security.CryptoService
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import core.models.Account.{AccountCreateResponse, AccountViewModel}
import core.models.AppUser.{AppUserInsert, AppUserViewModel}
import core.models.Application.AppRequestContext
import core.models.Registration.RegistrationHttpRequest
import infrastructure.repository.{AccountRepository, AppUserRepository}
import org.slf4j.LoggerFactory
import utils.StringUtilities

import java.time.Instant

trait RegistrationService {

  def registerAccount(
      request: RegistrationHttpRequest
  )(implicit
    appRequestContext: AppRequestContext
                     ): IO[Either[List[String], AccountCreateResponse]]
}

class RegistrationServiceImpl(
    accountRepository: AccountRepository,
    appUserRepository: AppUserRepository,
    cryptoService: CryptoService
) extends RegistrationService {
  private val logger = LoggerFactory.getLogger(getClass)

  def registerAccount(request: RegistrationHttpRequest)(implicit
      appRequestContext: AppRequestContext
  ): IO[Either[List[String], AccountCreateResponse]] = {
    AccountValidationService.validateAccount(request) match {
      case Valid(r) =>
        for {
          accountId <- accountRepository.createAccount(
            request.accountName.getOrElse("")
          )
          userId <- appUserRepository.createUser(
            mapRegistrationRequestToUserDataAccess(
              request = request,
              accountId = accountId
            )
          )
          _ = logger.info(s"Successfully created account with id $accountId")
        } yield Right(
          AccountCreateResponse(
            id = cryptoService.encodeHashId(accountId)
          )
        )
      case Invalid(errors) =>
        IO.pure(Left(errors.toList.map(_.message)))
    }
  }

  private def mapRegistrationRequestToUserDataAccess(
      request: RegistrationHttpRequest,
      accountId: Long
  )(implicit appRequestContext: AppRequestContext): AppUserInsert = {
    AppUserInsert(
      appLastChangedBy = cryptoService.decodeHashId(appRequestContext.id),
      accountId = accountId,
      encryptedEmail = StringUtilities.convertBytesToBase64(cryptoService.encrypt(request.email)),
      username = request.username,
      password = cryptoService.hashPassword(request.password),
      encryptedName =
        StringUtilities.convertBytesToBase64(cryptoService.encrypt(s"${request.firstName}:${request.lastName}")),
      encryptedPhone = StringUtilities.convertBytesToBase64(cryptoService.encrypt(request.phone)),
      lastLoginIp = appRequestContext.ipAddress,
      phoneVerified = Some(false),
      emailVerified = Some(false)
    )
  }
}
