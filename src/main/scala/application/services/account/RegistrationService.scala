package application.services.account

import application.services.security.{CryptoService, TokenService}
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import core.identity.{AccessTokenResponse, TokenType}
import core.models.Account.AccountCreateResponse
import core.models.AppUser.AppUserInsert
import core.models.Registration.RegistrationHttpRequest
import infrastructure.repository.{AccountRepository, AppUserRepository}
import org.slf4j.LoggerFactory
import utils.StringUtilities

import java.time.{Duration, Instant}
import java.util.{Date, UUID}

trait RegistrationService {

  def registerAccount(
      request: RegistrationHttpRequest
  ): IO[Either[List[String], AccountCreateResponse]]
}

class RegistrationServiceImpl(
    accountRepository: AccountRepository,
    appUserRepository: AppUserRepository,
    cryptoService: CryptoService,
    tokenService: TokenService
) extends RegistrationService {
  private val logger = LoggerFactory.getLogger(getClass)

  def registerAccount(
      request: RegistrationHttpRequest
  ): IO[Either[List[String], AccountCreateResponse]] = {
    AccountValidationService.validateAccount(request) match {
      case Valid(r) =>
        for {
          dbTransaction <- accountRepository.createAccount(
            request.accountName.getOrElse(""),
            mapRegistrationRequestToUserDataAccess(request)
          )
          _ = logger.info(
            s"Successfully created account with id ${dbTransaction._1}"
          ) //accountId
          tokenResponse = generateTokenFromRegistration(
            cryptoService.encodeHashId(dbTransaction._2)
          ) // userId
        } yield Right(
          AccountCreateResponse(
            accountId = cryptoService.encodeHashId(dbTransaction._1),
            sessionId = tokenResponse.sessionId,
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken,
            accessTokenExpiration = tokenResponse.accessTokenExpiration,
            refreshTokenExpiration = tokenResponse.refreshTokenExpiration
          )
        )
      case Invalid(errors) =>
        IO.pure(Left(errors.toList.map(_.message)))
    }
  }

  private def generateTokenFromRegistration(
      userId: String
  ): AccessTokenResponse = {
    val now = Instant.now()
    val accessToken = tokenService.generateToken(
      userId,
      tokenType = TokenType.Access
    )
    val refreshToken = tokenService.generateToken(
      userId,
      tokenType = TokenType.Refresh
    )
    
    AccessTokenResponse(
      sessionId = UUID.randomUUID(),
      accessToken = accessToken.token,
      refreshToken = refreshToken.token,
      accessTokenExpiration = accessToken.expiration,
      refreshTokenExpiration = refreshToken.expiration
    )
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
