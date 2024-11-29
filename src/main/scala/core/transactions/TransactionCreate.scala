package core.transactions

case class TransactionCreate(
    userId: String,
    accountId: String,
    transactionAmount: BigDecimal,
    transactionDate: BigInt, // API contracts should send over the date in the GMT epoch
    location: String,
    pending: Boolean,
    recurringTransactionId: Option[String],
    lineItems: Seq[TransactionLineItem]
)
