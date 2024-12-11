package core.transactions.sql.codecs

import core.transactions.sql.TransactionSql
import doobie.postgres.implicits.*
import doobie.{Meta, Read}
import utils.BaseSqlCodecs.bigIntMeta

import java.time.Instant

object TransactionSchemaMapping {
  implicit val transactionSqlRead: Read[TransactionSql] =
    Read[
      (Long, Long, Instant, String, BigDecimal, Option[String])
    ].map {
      case (
            id,
            userId,
            transactionDate,
            location,
            transactionAmount,
            transactionName
          ) =>
        TransactionSql(
          id,
          userId,
          transactionDate,
          location,
          transactionAmount,
          transactionName
        )
    }
}
