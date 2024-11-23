package application.account

import application.services.account.RegistrationServiceImpl
import application.services.security.{CryptoService, TokenService}
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import core.models.Account.{Account, AccountCreateResponse}
import core.models.AppUser.{AppUser, AppUserInsert}
import core.models.Registration.RegistrationHttpRequest
import core.validation.ValidationErrors
import doobie.free.*
import infrastructure.repository.{AccountRepository, AppUserRepository}
import org.hashids.Hashids
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import utils.TestHelpers

class RegistrationServiceSpec extends AnyFlatSpec with Matchers {
  private implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global
  private final val testConfig = TestHelpers.testConfig

  private final val hashIds = Hashids(testConfig.securityConfig.hashIdSalt, 8)

  val accountRepositoryMock: AccountRepository = new AccountRepository {
    override def createAccount(
        accountName: String,
        appUserInsert: AppUserInsert
    ): IO[(Long, Long)] = IO.pure(1, 1)

    override def getAccount(id: Long): IO[Option[Account]] = IO.pure(None)

    override def updateAccount(id: Long, accountName: String): IO[Int] =
      IO.pure(1)
  }

  val appUserRepoMock: AppUserRepository = new AppUserRepository {
    override def createUser(
        createdUser: AppUserInsert
    ): doobie.ConnectionIO[Long] = connection.pure(1L)

    override def createUserWithCommit(createdUser: AppUserInsert): IO[Long] =
      IO.pure(1)

    override def getUser(id: Long): IO[Option[AppUser]] = IO.pure(None)

    override def updateAccountLastChangeBy(
        id: Long
    ): doobie.ConnectionIO[Int] = connection.pure(1)

    override def getUserByEmail(encryptedEmail: String): IO[Option[String]] =
      IO.pure(None)
  }

  val cryptoService = new CryptoService(testConfig, hashIds)
  val tokenService = new TokenService(testConfig)

  var registrationService = new RegistrationServiceImpl(
    accountRepositoryMock,
    appUserRepoMock,
    cryptoService,
    tokenService
  )

  "RegistrationServiceSpec" should "create a valid account" in {

    val validAccount = RegistrationHttpRequest(
      accountName = Some("My Family"),
      username = "user1",
      email = "user1@test.com",
      password = "super-secret123",
      passwordConfirmation = "super-secret123",
      phone = "1231231234",
      firstName = "Test",
      lastName = "User"
    )

    val result =
      registrationService.registerAccount(validAccount).unsafeRunSync()
    result.isRight shouldBe true

  }

  it should "validate against an invalid account creation request " in {
    val invalidAccount = RegistrationHttpRequest(
      accountName = None,
      username = "",
      email = "",
      password = "",
      passwordConfirmation = "",
      phone = "",
      firstName = "",
      lastName = ""
    )

    val result =
      registrationService.registerAccount(invalidAccount).unsafeRunSync()
    result shouldBe Left(
      List(
        ValidationErrors.InvalidEmail.message,
        ValidationErrors.FirstNameNotEmpty.message,
        ValidationErrors.LastNameNotEmpty.message,
        ValidationErrors.PasswordNotEmpty.message
      )
    )
  }
}
