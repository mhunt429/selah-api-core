package core.transactions.sql

import java.time.Instant

case class TransactionCreateSql(
    userId: BigInt,
    accountId: BigInt,
    transactionAmount: BigDecimal,
    transactionDate: Instant,
    location: String,
    pending: Boolean,
    recurringTransactionId: Option[BigInt]
)
