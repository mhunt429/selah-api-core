package core.transactions

import java.time.Instant

case class RecentTransactionSummary(
    transactionId: String, 
    date: Instant,
    amount: BigDecimal,
    location: String,
    accountName: String
)
