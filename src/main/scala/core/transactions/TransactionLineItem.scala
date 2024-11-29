package core.transactions

//Model for handling splitting transactions across 1 or multiple categories
case class TransactionLineItem(
    categoryId: String,
    itemizedAmount: BigDecimal
)
