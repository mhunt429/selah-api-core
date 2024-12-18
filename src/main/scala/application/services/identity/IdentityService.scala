package application.services.identity

import application.services.security.{CryptoService, TokenService}
import cats.effect.IO
import core.identity.{LoginRequest, LoginResponse}
import infrastructure.repository.AppUserRepository
import utils.mappings.AppUserMappings

class IdentityService(
    cryptoService: CryptoService,
    tokenService: TokenService,
    appUserRepository: AppUserRepository
) {

  def logUserIn(loginRequest: LoginRequest): IO[Option[LoginResponse]] = {
    appUserRepository.getUserByUsername(loginRequest.username).map {
      case Some(dbUser) => {
        if (
          cryptoService.checkPassword(loginRequest.password, dbUser.password)
        ) {
          val user =
            AppUserMappings.getUserViewModelFromDb(dbUser, cryptoService)
          val token = tokenService.generateAccessTokenResponse(user.id)
          Some(LoginResponse(user = user, tokenData = token))
        } else {
          None
        }
      }
      case _ => None
    }
  }
}
