package core.transactions

import java.time.Instant

case class Transaction(
    id: String,
    categoryId: String,
    transactionDate: Instant,
    location: String,
    transactionAmount: BigDecimal,
    transactionName: Option[String]
)
