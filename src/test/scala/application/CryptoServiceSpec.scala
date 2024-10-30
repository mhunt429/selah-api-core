package application

import application.services.security.CryptoService
import org.hashids.Hashids
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import utils.TestHelpers

import java.util.Base64

class CryptoServiceSpec extends AnyFlatSpec with Matchers {
  private final val testConfig = TestHelpers.testConfig

  private final val hashIds = Hashids(testConfig.securityConfig.hashIdSalt, 8)

  val cryptoService: CryptoService = new CryptoService(testConfig, hashIds)

  it should "encrypt and decrypt should return original text" in {
    val plainText = "Hello World!"
    val encryptedData = cryptoService.encrypt(plainText)
    encryptedData should not be empty

    val decryptedText = cryptoService.decrypt(encryptedData)
    decryptedText shouldBe plainText
  }

  it should "create two separate encrypted values for the same input" in {
    val string1 = "abc123"
    val string2 = "abc123"

    val encryptedString1 = cryptoService.encrypt(string1)
    val encryptedString2 = cryptoService.encrypt(string2)

    Base64.getEncoder.encodeToString(
      encryptedString1
    ) should not be Base64.getEncoder.encodeToString(encryptedString2)
  }

  it should "decodeHashId should return correct id for a given encoded hash" in {
    val id = 12345L
    val encoded = cryptoService.encodeHashId(id)
    val decoded = cryptoService.decodeHashId(encoded)

    decoded shouldBe id
  }

  it should "decodeHashId should return 0 for an invalid hash" in {
    val invalidId = "invalidHash"
    val decoded = cryptoService.decodeHashId(invalidId)

    decoded shouldBe 0L
  }

  it should "encodeHashId should return a non-empty string" in {
    val id = 67890L
    val encoded = cryptoService.encodeHashId(id)

    encoded should not be empty
    cryptoService.decodeHashId(encoded) shouldBe id
  }

}
