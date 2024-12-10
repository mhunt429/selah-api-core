package core.transactions.sql

import java.time.Instant

case class RecurringTransactionSql(
    id: BigInt,
    userId: BigInt,
    recurringTransactionId: Option[BigInt],
    upcomingDate: Option[Instant],
    lastPaidDate: Option[Instant],
    location: String,
    frequency: String
)
