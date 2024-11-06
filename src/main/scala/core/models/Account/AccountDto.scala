package core.models.Account
import java.time.Instant

case class AccountViewModel(
    id: String,
    dateCreated: Instant,
    accountName: Option[String]
)

case class AccountCreateResponse(
    id: String
)

case class AccountCreateRequest(
    accountName: Option[String]
)

case class AccountUpdateRequest(
    id: String,
    accountName: Option[String]
)
