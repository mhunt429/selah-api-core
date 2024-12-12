package core.transactions.sql

import java.time.Instant
case class TransactionLineItemSql(
    id: Long,
    transactionId: Long,
    transactionCategoryId: String,
    itemizedAmount: BigDecimal
)

case class TransactionLineItemInsertSql(
    appContextUserId: Long,
    originalInsert: Instant = Instant.now(),
    lastUpdate: Instant = Instant.now(),
    transactionId: Long,
    transactionCategoryId: Long,
    itemizedAmount: BigDecimal
)

case class TransactionLineItemUpdateSql(
    transactionCategoryId: Long,
    itemizedAmount: BigDecimal
)
