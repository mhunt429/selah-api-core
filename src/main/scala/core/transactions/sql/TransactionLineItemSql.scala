package core.transactions.sql

import doobie.*
import doobie.util.Write
case class TransactionLineItemSql(
    id: Long,
    transactionId: Long,
    categoryId: String,
    itemizedAmount: BigDecimal
)

case class TransactionLineItemInsertSql(
    transactionId: Long,
    categoryId: Long,
    itemizedAmount: BigDecimal
)

case class TransactionLineItemUpdateSql(
    categoryId: Long,
    itemizedAmount: BigDecimal
)
