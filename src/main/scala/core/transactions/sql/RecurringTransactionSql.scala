package core.transactions.sql

import core.transactions.{RecurringTransactionFrequency, RecurringTransactionNotificationPreferences}

import java.time.Instant

case class RecurringTransactionSql(
    id: BigInt,
    upcomingDate: Option[Instant],
    lastPaidDate: Option[Instant],
    location: String,
    frequency: RecurringTransactionFrequency,
    notificationPreference: Option[RecurringTransactionNotificationPreferences]
)
