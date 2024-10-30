package core.models.Account

import java.time.Instant

case class Account(
    originalInsert: Instant,
    lastUpdate: Instant,
    appLastChangedBy: Long,
    id: Long,
    dateCreated: Instant,
    accountName: Option[String]
)

case class AccountInsert(
    originalInsert: Instant = Instant.now(),
    lastUpdate: Instant = Instant.now(),
    appLastChangedBy: Long,
    dateCreated: Instant = Instant.now(),
    accountName: Option[String]
)

case class AccountUpdate(
    id: Long,
    lastUpdate: Instant = Instant.now(),
    appLastChangedBy: Long,
    accountName: Option[String]
)
