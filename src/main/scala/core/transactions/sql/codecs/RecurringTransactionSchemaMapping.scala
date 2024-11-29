package core.transactions.sql.codecs

import core.transactions.sql.RecurringTransactionSql
import doobie.{Meta, Read}
import doobie.postgres.implicits.*
import java.time.Instant
import utils.BaseSqlCodecs._
object RecurringTransactionSchemaMapping {


  
  implicit val recurringTransactionSqlRead: Read[RecurringTransactionSql] =
    Read[
      (BigInt, Option[Instant], Option[Instant], String, String, Option[String])
    ].map {
      case (
            id,
            upcomingDate,
            lastPaidDate,
            location,
            frequency,
            notificationPreference
          ) =>
        RecurringTransactionSql(
          id,
          upcomingDate,
          lastPaidDate,
          location,
          frequency,
          notificationPreference
        )
    }
}
