package core.transactions.sql

case class TransactionCategoryCreateSql(
    appLastChangedBy: Long,
    userId: Long,
    name: String
)
