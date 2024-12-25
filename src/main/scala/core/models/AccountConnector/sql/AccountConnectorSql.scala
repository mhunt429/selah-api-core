package core.models.AccountConnector.sql

import java.time.Instant

case class AccountConnectorSqlInsert(
    appLastChangedBy: Long,
    userId: Long,
    institutionId: String,
    institutionName: String,
    dateConnected: Instant = Instant.now(),
    encryptedAccessToken: String
)
