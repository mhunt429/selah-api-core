package application.services.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import core.config.Config
import utils.StringUtilities

import java.util.Date
import javax.crypto.Cipher

class TokenService(config: Config) {

  private final val ISSUER = "selah-api"
  private final val SECRET = config.securityConfig.jwtSecret
  private final val VALIDITY_DURATION =
    2 * 60 * 60 * 1000 // 2 hours in milliseconds
  private final val ALGO = Algorithm.HMAC256(SECRET)
  final lazy val VERIFIER = JWT.require(ALGO).withIssuer(ISSUER).build()
  def generateToken(userId: String): String = {
    val now = new Date
    val expiration = new Date(now.getTime + VALIDITY_DURATION)
    JWT
      .create()
      .withIssuer(ISSUER)
      .withExpiresAt(expiration)
      .withClaim("sub", userId)
      .sign(ALGO)
  }

  private def verifyAndDecodeJWT(jwt: String): DecodedJWT = {
    try {
      JWT.require(ALGO).build().verify(jwt.replace("Bearer ", ""))
    } catch {
      case jve: JWTVerificationException =>
        throw new Exception(jve.getLocalizedMessage)
    }
  }

  def getUserIdFromJWT(jwt: String): String = {
    verifyAndDecodeJWT(jwt).getClaim("sub").asString()
  }

}
