package core.models.Banking

case class BalanceUpdate(
    accountId: String,
    currentBalance: BigDecimal
)
