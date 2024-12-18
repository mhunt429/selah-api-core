package utils.mappings

import application.services.security.CryptoService
import core.models.AppUser.AppUserViewModel
import core.models.AppUser.sql.AppUser
import utils.StringUtilities

object AppUserMappings {

  //Takes the result from app_user table and maps to our view model to expose view API endpoints
  def getUserViewModelFromDb(
      user: AppUser,
      cryptoService: CryptoService
  ): AppUserViewModel = {
    val fullName = cryptoService
      .decrypt(StringUtilities.convertBase64ToBytes(user.encryptedName))
    val splitName = fullName.split(":")
    AppUserViewModel(
      id = cryptoService.encodeHashId(user.id),
      accountId = cryptoService.encodeHashId(user.accountId),
      email = cryptoService
        .decrypt(
          StringUtilities.convertBase64ToBytes(user.encryptedEmail)
        ),
      username = user.username,
      firstName = splitName.head,
      lastName = splitName.last,
      phone = cryptoService
        .decrypt(
          StringUtilities.convertBase64ToBytes(user.encryptedPhone)
        ),
      dateCreated = user.createdDate
    )
  }

}
