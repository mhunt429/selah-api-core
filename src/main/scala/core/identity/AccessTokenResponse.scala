package core.identity
import java.util.{Date, UUID}

case class AccessTokenResponse(
    sessionId: UUID,
    accessToken: String,
    refreshToken: String,
    accessTokenExpiration: Date,
    refreshTokenExpiration: Date
)
