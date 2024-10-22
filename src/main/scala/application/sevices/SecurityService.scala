package application.sevices
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import core.config.Config
import org.hashids.Hashids

import java.security.SecureRandom
import java.util.Date
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

class SecurityService(hashService: Hashids, config: Config) {

  private final val ISSUER = "selah-api"
  private final val SECRET = config.securityConfig.jwtSecret
  private final val VALIDITY_DURATION =
    2 * 60 * 60 * 1000 // 2 hours in milliseconds
  private final val ALGO = Algorithm.HMAC256(SECRET)
  private final val AES_KEY =
    config.securityConfig.cryptoIv // AES key from config
  private final val AES_IV = generateIv() // Generate IV or get from config
  final lazy val VERIFIER = JWT.require(ALGO).withIssuer(ISSUER).build()
  private final val CRYPTO_CIPHER = Cipher.getInstance("AES/CBC/PKCS5Padding")
  private final val SECRET_KEY =
    new SecretKeySpec(AES_KEY.getBytes("UTF-8"), "AES")

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

  def decodeHashId(id: String): Long = {
    //HashIds returns a list of ints but if bad id gets sent over, it will return a empty list
    //If that is that case, just convert that to an option and return 0 if the list is empty
    hashService.decode(id).toList.headOption.getOrElse(0L)
  }
  def encodeHashId(id: Long): String = hashService.encode(id)

  def encrypt(plainText: String): Array[Byte] = {
    val secretKey = new SecretKeySpec(AES_KEY.getBytes("UTF-8"), "AES")
    val ivSpec = new IvParameterSpec(AES_IV)
    CRYPTO_CIPHER.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
    val encryptedBytes = CRYPTO_CIPHER.doFinal(plainText.getBytes("UTF-8"))
    encryptedBytes
  }

  def decrypt(encryptedBytes: Array[Byte]): String = {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    val ivSpec = new IvParameterSpec(AES_IV)

    cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, ivSpec)

    val decryptedBytes = cipher.doFinal(encryptedBytes)

    new String(decryptedBytes, "UTF-8")
  }

  private def generateIv(): Array[Byte] = {
    val secureRandom = new SecureRandom()
    Array.fill(16)(secureRandom.nextInt(256).toByte)
  }
}
