package application.account

import application.services.account.AccountValidationService
import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import core.models.AppUser.AppUserCreateRequest
import core.validation.{ValidationError, ValidationErrors}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.Instant

class AccountValidationServiceSpec extends AnyFlatSpec with Matchers {

  it should "return a list of errors an invalid created user" in {
    val invalidUser = AppUserCreateRequest("", "", "", "", "", "", "", "")

    val validationResult = AccountValidationService.validateUser(invalidUser)

    validationResult shouldBe Invalid(
      NonEmptyList.of(
        ValidationErrors.InvalidEmail,
        ValidationErrors.FirstNameNotEmpty,
        ValidationErrors.LastNameNotEmpty,
        ValidationErrors.AccountIdEmpty
      )
    )

  }

  it should "validate against a valid created user" in {
    val validUser = AppUserCreateRequest(
      email = "test@example.com",
      username = "user",
      firstName = "John",
      lastName = "Doe",
      accountId = "validAccountId",
      password = "password",
      passwordConfirmation = "password",
      dateCreated = Instant.now,
      phone = "1231231234"
    )

    val validationResult = AccountValidationService.validateUser(validUser)

    validationResult shouldBe Valid(validUser)
  }

}
