package application.services.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import core.config.Config
import core.identity.TokenType
import utils.DateUtilities

import java.time.Duration
import java.time.Instant.now
import java.util.Date

case class GenerateTokenResponse(token: String, expiration: Date)

class TokenService(config: Config) {

  private final val ISSUER = "selah-api"

  private final val SECRET = config.securityConfig.jwtSecret

  private final val ALGO = Algorithm.HMAC256(SECRET)

  final lazy val VERIFIER = JWT.require(ALGO).withIssuer(ISSUER).build()

  def generateToken(userId: String, tokenType: String): GenerateTokenResponse = {
    val expiration = tokenType match {
      case TokenType.Access =>
        DateUtilities.addMinutesToCurrentTime(config.securityConfig.accessTokenExpiryMinutes)
      case TokenType.Refresh =>
        DateUtilities.addDaysToCurrentTime(config.securityConfig.refreshTokenExpiryDays)
      case _ =>
        throw new IllegalArgumentException(s"Unsupported token type: $tokenType")
    }

    GenerateTokenResponse(
      token = createAuthScopeToken(userId, tokenType, expiration),
      expiration = expiration
    )
  }
  
  private def createAuthScopeToken(userId: String, tokenType: String, expiration: Date ): String =
    JWT
      .create()
      .withIssuer(ISSUER)
      .withExpiresAt(expiration)
      .withClaim("sub", userId)
      .withClaim("type", tokenType)
      .sign(ALGO)
}
