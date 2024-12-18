package core.identity

import core.models.AppUser.AppUserViewModel

case class LoginRequest(
    username: String,
    password: String
)

case class LoginResponse(
    user: AppUserViewModel,
    tokenData: AccessTokenResponse
)
