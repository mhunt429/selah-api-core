package core.transactions

case class TransactionCategory(
    id: String,
    userId: String,
    name: String
)

case class TransactionCategoryCreate(
    userId: String,
    name: String
)

case class TransactionCategoryUpdate(
    name: String
)
