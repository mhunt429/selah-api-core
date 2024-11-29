package core.models.Banking

case class BankAccount(
    id: String,
    accountMask: String,
    accountName: Option[String],
    currentBalance: BigDecimal,
    userId: String,
    subtype: String //i.e Credit/Debit/Checking/Savings
)
