package http.account

import api.routes.account.RegistrationRoutes
import application.services.account.RegistrationService
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import core.json.AccountJson.*
import core.models.Account.AccountCreateResponse
import core.models.Registration.RegistrationHttpRequest
import io.circe.syntax.*
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.client.dsl.io.*
import org.http4s.syntax.literals.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.{Date, UUID}

class RegistrationHttpSpec extends AnyFlatSpec with Matchers {
  private implicit val runtime: IORuntime = IORuntime.global
  private val registrationUri = uri"/account/register"

  private val mockRegistrationService = new RegistrationService {
    override def registerAccount(
        request: RegistrationHttpRequest
    ): IO[Either[List[String], AccountCreateResponse]] =
      IO.pure(
        Right(
          AccountCreateResponse(
            "123",
            UUID.randomUUID(),
            "accessToken",
            "refreshToken",
            new Date(),
            new Date()
          )
        )
      )
  }

  private val mockFailureRegistrationService = new RegistrationService {
    override def registerAccount(
        request: RegistrationHttpRequest
    ): IO[Either[List[String], AccountCreateResponse]] =
      IO.pure(
        Left(
          List(
            "Invalid email",
            "Password does not meet complexity requirements"
          )
        )
      )
  }

  "RegistrationRoutes" should "return 201 on successful account creation" in {
    val registrationHttpRequest = RegistrationHttpRequest(
      accountName = Some("My Family"),
      username = "user1",
      email = "user1@test.com",
      password = "super-secret123",
      passwordConfirmation = "super-secret123",
      phone = "1231231234",
      firstName = "Test",
      lastName = "User"
    )

    val req = POST(
      registrationHttpRequest.asJson,
      registrationUri
    )
    val routes = RegistrationRoutes(mockRegistrationService).routes
    val response = routes.run(req)
    response.map(_.status).value.unsafeRunSync().get shouldBe Status.Created
  }

  it should "return 400 when the registrationService returns validation errors" in {
    val invalidRegistrationHttpRequest = RegistrationHttpRequest(
      accountName = None,
      username = "user1",
      email = "invalid-email",
      password = "short",
      passwordConfirmation = "short",
      phone = "1231231234",
      firstName = "Test",
      lastName = "User"
    )

    val req = POST(
      invalidRegistrationHttpRequest.asJson,
      registrationUri
    )
    val routes = RegistrationRoutes(mockFailureRegistrationService).routes
    val response = routes.run(req)

    // Validate the response status
    response.map(_.status).value.unsafeRunSync().get shouldBe Status.BadRequest

  }
}
