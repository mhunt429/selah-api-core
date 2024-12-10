package core.transactions.sql.codecs

import core.transactions.sql.RecurringTransactionSql
import doobie.postgres.implicits.*
import doobie.{Meta, Read}
import utils.BaseSqlCodecs.*

import java.time.Instant
object RecurringTransactionSchemaMapping {

  implicit val recurringTransactionSqlRead: Read[RecurringTransactionSql] =
    Read[
      (
          BigInt,
          BigInt,
          Option[BigInt],
          Option[Instant],
          Option[Instant],
          String,
          String
      )
    ].map {
      case (
            id,
            userId,
            recurringTransactionId,
            upcomingDate,
            lastPaidDate,
            location,
            frequency
          ) =>
        RecurringTransactionSql(
          id,
          userId,
          recurringTransactionId,
          upcomingDate,
          lastPaidDate,
          location,
          frequency
        )
    }
}
