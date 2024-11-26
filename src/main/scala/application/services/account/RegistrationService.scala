package application.services.account

import application.services.security.{CryptoService, TokenService}
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import core.identity.{AccessTokenResponse, TokenType}
import core.models.Account.AccountCreateResponse
import core.models.AppUser.sql.AppUserInsert
import core.models.Registration.RegistrationHttpRequest
import infrastructure.repository.{AccountRepository, AppUserRepository}
import org.slf4j.LoggerFactory

import java.time.Instant
import java.util.UUID

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
    val encryptedEmail =
      cryptoService.encryptToBase64(request.email)
    val password = cryptoService.hashPassword(request.password)
    val encryptedName =
      cryptoService.encryptToBase64(s"${request.firstName}:${request.lastName}")

    val encryptedPhone =
      cryptoService.encryptToBase64(request.phone)

    AppUserInsert(
      //The -1(s) will get updated by the transaction chains
      appLastChangedBy = -1,
      accountId = -1,
      encryptedEmail = encryptedEmail,
      username = request.username,
      password = password,
      encryptedName = encryptedName,
      encryptedPhone = encryptedPhone,
      lastLoginIp = None,
      phoneVerified = Some(false),
      emailVerified = Some(false)
    )
  }
}
