package core.transactions.sql

import java.time.Instant

case class TransactionSql(
    id: BigInt,
    userId: BigInt,
    transactionDate: Instant,
    location: String,
    transactionAmount: BigDecimal,
    transactionName: Option[String]
)
