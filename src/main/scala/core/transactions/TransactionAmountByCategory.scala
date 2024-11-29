package core.transactions

case class TransactionAmountByCategory(
    categoryId: String,
    categoryName: String,
    total: BigDecimal
)
