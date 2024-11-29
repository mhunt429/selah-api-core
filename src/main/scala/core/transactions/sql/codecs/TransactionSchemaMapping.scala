package core.transactions.sql.codecs

import core.transactions.sql.TransactionSql
import doobie.{Meta, Read}
import doobie.postgres.implicits.*
import utils.BaseSqlCodecs.bigIntMeta
import java.time.Instant

object TransactionSchemaMapping {
  implicit val transactionSqlRead: Read[TransactionSql] =
    Read[
      (BigInt, BigInt, Instant, String, BigDecimal, Option[String])
    ].map {
      case (
        id,
        categoryId,
        transactionDate,
        location,
        transactionAmount,
        transactionName
        ) =>
        TransactionSql(
          id,
          categoryId,
          transactionDate,
          location,
          transactionAmount,
          transactionName
        )
    }
}
