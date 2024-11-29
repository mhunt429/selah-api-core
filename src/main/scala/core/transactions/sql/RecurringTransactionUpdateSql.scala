package core.transactions.sql

import java.time.Instant

case class RecurringTransactionUpdateSql(
    upcomingDate: Option[Instant],
    lastPaidDate: Option[Instant],
    location: String,
    frequency: String,
    notificationPreference: Option[String]
)
