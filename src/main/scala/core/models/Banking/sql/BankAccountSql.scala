package core.models.Banking.sql

case class BankAccountSql(
    id: BigInt,
    accountMask: String,
    accountName: Option[String],
    currentBalance: BigDecimal,
    userId: BigInt,
    subtype: String //i.e Credit/Debit/Checking/Savings
)
