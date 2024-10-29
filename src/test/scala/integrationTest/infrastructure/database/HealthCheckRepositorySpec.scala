package integrationTest.infrastructure.database

import cats.effect.unsafe.IORuntime
import cats.effect.{IO, Resource}
import infrastructure.repository.HealthCheckRepositoryImpl
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, EitherValues}
import utils.TestHelpers

class HealthCheckRepositorySpec
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach
    with EitherValues {

  private implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

  private var healthCheckRepositoryResource
      : Resource[IO, HealthCheckRepositoryImpl] = _

  override def beforeEach(): Unit = {
    healthCheckRepositoryResource = for {
      transactor <- TestHelpers.initializeTestDb()
    } yield new HealthCheckRepositoryImpl(transactor)
  }

  override def afterEach(): Unit = {
    // Manually release resources after each test if needed
    healthCheckRepositoryResource = null
  }

  it should "return postgres process id" in {
    healthCheckRepositoryResource
      .use { healthCheckRepository =>
        for {
          processId <- healthCheckRepository.getPostgresProcessId
          _ <- IO {
            processId.head should not be empty
          }
        } yield ()
      }
      .unsafeRunSync()
  }
}
