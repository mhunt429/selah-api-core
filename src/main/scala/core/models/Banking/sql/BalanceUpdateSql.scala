package core.models.Banking.sql

case class BalanceUpdateSql(
    accountId: BigDecimal,
    currentBalance: BigDecimal
)
