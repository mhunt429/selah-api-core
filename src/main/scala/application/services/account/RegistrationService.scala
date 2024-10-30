package application.services.account

import cats.effect.IO
import core.models.Registration.RegistrationHttpRequest
import infrastructure.repository.{AccountRepository, AppUserRepository}
import org.slf4j.LoggerFactory

trait RegistrationService {
  
  def registrationAccount(request: RegistrationHttpRequest): IO[Long]
}

class RegistrationServiceImp(accountRepository: AccountRepository, 
                             appUserRepository: AppUserRepository
)  {
  private val logger = LoggerFactory.getLogger(getClass)
}