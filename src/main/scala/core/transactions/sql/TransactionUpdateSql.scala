package core.transactions.sql

import java.time.Instant

case class TransactionUpdateSql(
    accountId: BigInt,
    transactionAmount: BigDecimal,
    transactionDate: Instant,
    location: String,
    pending: Boolean,
    recurringTransactionId: Option[BigInt]
)
