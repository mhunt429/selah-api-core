package core.transactions.sql

import java.time.Instant

case class TransactionSql(
    id: Long,
    userId: Long,
    transactionDate: Instant,
    location: String,
    transactionAmount: BigDecimal,
    transactionName: Option[String]
)
