package application.services.account

import application.services.AccountValidationService
import application.services.security.CryptoService
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import core.models.Account.AccountCreateResponse
import core.models.AppUser.AppUserInsert
import core.models.Registration.RegistrationHttpRequest
import infrastructure.repository.{AccountRepository, AppUserRepository}
import org.slf4j.LoggerFactory
import utils.StringUtilities

import java.time.Instant

trait RegistrationService {

  def registerAccount(
      request: RegistrationHttpRequest
  ): IO[Either[List[String], AccountCreateResponse]]
}

class RegistrationServiceImpl(
    accountRepository: AccountRepository,
    appUserRepository: AppUserRepository,
    cryptoService: CryptoService
) extends RegistrationService {
  private val logger = LoggerFactory.getLogger(getClass)

  def registerAccount(
      request: RegistrationHttpRequest
  ): IO[Either[List[String], AccountCreateResponse]] = {
    AccountValidationService.validateAccount(request) match {
      case Valid(r) =>
        for {
          accountId <- accountRepository.createAccount(
            request.accountName.getOrElse(""),
            mapRegistrationRequestToUserDataAccess(request)
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
      request: RegistrationHttpRequest
  ): AppUserInsert = {
    AppUserInsert(
      //The -1(s) will get updated by the transaction chains
      appLastChangedBy = -1,
      accountId = -1,
      encryptedEmail = StringUtilities.convertBytesToBase64(
        cryptoService.encrypt(request.email)
      ),
      username = request.username,
      password = cryptoService.hashPassword(request.password),
      encryptedName = StringUtilities.convertBytesToBase64(
        cryptoService.encrypt(s"${request.firstName}:${request.lastName}")
      ),
      encryptedPhone = StringUtilities.convertBytesToBase64(
        cryptoService.encrypt(request.phone)
      ),
      lastLoginIp = None,
      phoneVerified = Some(false),
      emailVerified = Some(false)
    )
  }
}
