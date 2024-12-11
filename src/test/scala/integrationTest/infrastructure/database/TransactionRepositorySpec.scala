package integrationTest.infrastructure.database

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import core.models.Account.AccountInsert
import core.models.AppUser.sql.AppUserInsert
import core.transactions.sql.{TransactionCreateSql, TransactionLineItemInsertSql}
import doobie.util.transactor.Transactor
import infrastructure.repository.{AccountRepositoryImpl, AppUserRepositoryImpl, TransactionRepositoryImpl}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, EitherValues}
import utils.TestHelpers

import java.time.Instant

class TransactionRepositorySpec
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach
    with EitherValues {

  private implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

  private var transactionRepository: TransactionRepositoryImpl = _
  private var accountRepository: AccountRepositoryImpl = _
  private var userRepository: AppUserRepositoryImpl = _
  private var transactor: Transactor[IO] = _
  override def beforeEach(): Unit = {

    val initResource = for {
      transactor <- TestHelpers.initializeTestDb()
    } yield new TransactionRepositoryImpl(transactor)

    transactionRepository = initResource.allocated.unsafeRunSync()._1
    startUpTasks()
  }

  override def afterEach(): Unit = { tearDownTasks() }

  "TransactionRepository" should "be able to insert a transaction" in {
    val transactionCreateSql = TransactionCreateSql(
      1,
      1,
      100,
      Instant.now(),
      "White Castle",
      false,
      None,
      Seq(TransactionLineItemInsertSql(1, 1, 100))
    )

    val trxId = transactionRepository
      .insertTransaction(transactionCreateSql)
      .unsafeRunSync()

    trxId should be > 0L
  }

  //Use this method to set up other data since we enforce FK constraints
  private def startUpTasks(): Unit = {
    val account =
      AccountInsert(appLastChangedBy = 1, accountName = Some("Mi Familia"))
    val accountId: Long =
      accountRepository.createAccount(account).unsafeRunSync()
    val user = AppUserInsert(
      appLastChangedBy = 1,
      accountId,
      encryptedEmail = "secret-email",
      username = "user",
      password = "@ssword",
      encryptedName = "secret-name",
      encryptedPhone = "secret-phone",
      lastLoginIp = Some("127.0.0.1"),
      phoneVerified = Some(true),
      emailVerified = Some(true)
    )

    val userId = userRepository.createUserWithCommit(user).unsafeRunSync()

  }

  private def tearDownTasks(): Unit = {}
}
