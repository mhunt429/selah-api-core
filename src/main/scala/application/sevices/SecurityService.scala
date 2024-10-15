package application.sevices
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import core.config.Config
import org.hashids.Hashids

import java.util.Date
import scala.util.Try

class SecurityService(hashService: Hashids, config: Config) {

  final val CLAIM_SUBJECT = "sub"
  final val ISSUER = "selah-api"
  final val SECRET = config.securityConfig.jwtSecret
  final val VALIDITY_DURATION = 2 * 60 * 60 * 1000 // 2 hours in milliseconds
  final val ALGO = Algorithm.HMAC256(SECRET)
  final lazy val verifier = JWT.require(ALGO).withIssuer(ISSUER).build()

  def generateToken(userId: String): String = {
    val now = new Date
    val expiration = new Date(now.getTime + VALIDITY_DURATION)
    JWT
      .create()
      .withIssuer(ISSUER)
      .withExpiresAt(expiration)
      .withClaim(CLAIM_SUBJECT, userId)
      .sign(ALGO)
  }

  def verifyAndDecodeJWT(jwt: String): DecodedJWT = {
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

  def decodeHashId(id: String): Long = {
    //HashIds returns a list of ints but if bad id gets sent over, it will return a empty list
    //If that is that case, just convert that to an option and return 0 if the list is empty
    hashService.decode(id).toList.headOption.getOrElse(0L)
  }
  def encodeHashId(id: Long): String = hashService.encode(id)
}
