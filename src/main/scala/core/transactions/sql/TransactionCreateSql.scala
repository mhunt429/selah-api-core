package core.transactions.sql

import java.time.Instant

case class TransactionCreateSql(
    appContextUserId: Long,
    userId: Long,
    accountId: Long,
    transactionAmount: BigDecimal,
    transactionDate: Instant,
    location: String,
    pending: Boolean,
    recurringTransactionId: Option[Long],
    lineItems: Seq[TransactionLineItemInsertSql]
)
