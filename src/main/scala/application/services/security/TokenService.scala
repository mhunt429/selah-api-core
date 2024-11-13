package application.services.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import core.config.Config
import core.identity.TokenType

import java.time.{Duration, Instant}
import java.util.Date

class TokenService(config: Config) {

  private final val ISSUER = "selah-api"

  private final val SECRET = config.securityConfig.jwtSecret

  private final val ALGO = Algorithm.HMAC256(SECRET)

  final lazy val VERIFIER = JWT.require(ALGO).withIssuer(ISSUER).build()

  def generateToken(
      userId: String,
      tokenType: String,
      expiration: Date
  ): String = {
    JWT
      .create()
      .withIssuer(ISSUER)
      .withExpiresAt(expiration)
      .withClaim("sub", userId)
      .withClaim("type", tokenType)
      .sign(ALGO)
  }
}