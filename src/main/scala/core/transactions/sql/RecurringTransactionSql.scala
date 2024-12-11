package core.transactions.sql

import java.time.Instant

case class RecurringTransactionSql(
    id: Long,
    userId: Long,
    recurringTransactionId: Option[Long],
    upcomingDate: Option[Instant],
    lastPaidDate: Option[Instant],
    location: String,
    frequency: String
)
