package application.services.security

import core.config.Config
import core.security.{CommonPII, EncryptedPII}
import org.hashids.Hashids
import org.mindrot.jbcrypt.BCrypt
import utils.StringUtilities

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

class CryptoService(config: Config, hashService: Hashids) {
  private final val AES_KEY =
    StringUtilities.hexStringToByteArray(config.securityConfig.cryptoSecret)

  private final val CRYPTO_CIPHER = Cipher.getInstance("AES/CBC/PKCS5Padding")

  def encrypt(plainText: String): Array[Byte] = {
    val secretKey = new SecretKeySpec(AES_KEY, "AES")
    val iv = generateIv() // Generate a new IV for each encryption
    val ivSpec = new IvParameterSpec(iv)

    val cryptoCipher =
      Cipher.getInstance("AES/CBC/PKCS5Padding")
    cryptoCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

    val encryptedBytes = cryptoCipher.doFinal(plainText.getBytes("UTF-8"))

    iv ++ encryptedBytes
  }

  def decrypt(encryptedData: Array[Byte]): String = {
    // Extract the IV from the first 16 bytes
    val iv = encryptedData.take(16)
    val cipherText = encryptedData.drop(16)

    val ivSpec = new IvParameterSpec(iv)

    val secretKey = new SecretKeySpec(AES_KEY, "AES")
    CRYPTO_CIPHER.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

    val decryptedBytes = CRYPTO_CIPHER.doFinal(cipherText)

    new String(decryptedBytes, "UTF-8")
  }

  def encryptToBase64(value: String): String =
    StringUtilities.convertBytesToBase64(encrypt(value))

  def decodeHashId(id: String): Long = {
    //HashIds returns a list of ints but if bad id gets sent over, it will return a empty list
    //If that is that case, just convert that to an option and return 0 if the list is empty
    hashService.decode(id).toList.headOption.getOrElse(0L)
  }

  def encodeHashId(id: Long): String = hashService.encode(id)

  private def generateIv(): Array[Byte] = {
    val secureRandom = new SecureRandom()
    Array.fill(16)(secureRandom.nextInt(256).toByte)
  }

  def hashPassword(plainText: String): String = {
    BCrypt.hashpw(plainText, BCrypt.gensalt())
  }

  def checkPassword(plainText: String, hashedPassword: String): Boolean = {
    BCrypt.checkpw(plainText, hashedPassword)
  }

  def encryptCommonPII(pii: CommonPII): EncryptedPII = {
    val encryptedEmail: String =
      StringUtilities.convertBytesToBase64(encrypt(pii.email))
    val encryptedPassword: String = hashPassword(pii.password)
    val encryptedName: String = StringUtilities.convertBytesToBase64(
      encrypt(s"${pii.firstName}:${pii.lastName}")
    )
    val encryptedPhone: String =
      StringUtilities.convertBytesToBase64(encrypt(pii.phone))

    EncryptedPII(
      encryptedEmail = encryptToBase64(pii.email),
      encryptedPassword = hashPassword(pii.password),
      encryptedName = encryptToBase64(s"${pii.firstName}:${pii.lastName}"),
      encryptedPhone = encryptToBase64(pii.phone)
    )
  }

}
