package core.models.Account
import java.time.Instant
import java.util.{Date, UUID}

case class AccountViewModel(
    id: String,
    dateCreated: Instant,
    accountName: Option[String]
)

case class AccountCreateResponse(
  accountId: String,
  sessionId: UUID,
  accessToken: String,
  refreshToken: String,
  accessTokenExpiration: Date,
  refreshTokenExpiration: Date
)


case class AccountCreateRequest(
    accountName: Option[String]
)

case class AccountUpdateRequest(
    id: String,
    accountName: Option[String]
)
