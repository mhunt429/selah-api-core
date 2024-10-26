package application

import application.sevices.CryptoService
import org.hashids.Hashids
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import utils.TestHelpers

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
