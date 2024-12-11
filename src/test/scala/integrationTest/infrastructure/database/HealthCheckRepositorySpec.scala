package integrationTest.infrastructure.database

import cats.effect.IO
import cats.effect.unsafe.IORuntime
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

  private var healthCheckRepository: HealthCheckRepositoryImpl = _

  override def beforeEach(): Unit = {
    val initResource = for {
      transactor <- TestHelpers.initializeTestDb()
    } yield new HealthCheckRepositoryImpl(transactor)

    // Run the Resource initialization and extract the value
    healthCheckRepository = initResource.allocated.unsafeRunSync()._1
  }

  it should "return postgres process id" in {
    val processId = healthCheckRepository.getPostgresProcessId.unsafeRunSync()
    processId.head should not be empty
  }
}
