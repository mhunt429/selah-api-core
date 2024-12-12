package infrastructure.repository
import cats.effect.IO
import core.transactions.sql.{TransactionCategoryCreateSql, TransactionCreateSql, TransactionLineItemInsertSql, TransactionUpdateSql}
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.TimestampMeta

import java.sql.Timestamp
import java.time.Instant

trait TransactionRepository {
  def insertTransaction(
      transactionCreateSql: TransactionCreateSql,
      appContextUserId: Long
  ): IO[Long]

  def insertTransactionLineItem(
      transactionId: BigDecimal,
      categoryId: String,
      itemizedAmount: BigDecimal
  ): ConnectionIO[Long]

  def updateTransaction(
      transactionUpdateSql: TransactionUpdateSql
  ): ConnectionIO[Long]

  def deleteTransaction(id: Long): ConnectionIO[Long]

  def createTransactionCategory(
      categoryCreateSql: TransactionCategoryCreateSql
  ): IO[Long]
}

class TransactionRepositoryImpl(xa: Transactor[IO])
    extends TransactionRepository {

  implicit val instantMeta: Meta[Instant] =
    Meta[Timestamp].imap(_.toInstant)(Timestamp.from)

  def insertTransaction(
      transactionCreateSql: TransactionCreateSql,
      appContextUserId: Long
  ): IO[Long] = {
    val dbTrx: ConnectionIO[Long] = for {
      transactionId <- BaseRepository.insertWithId(
        xa,
        insertTransactionSql(transactionCreateSql)
      )
      _ <- BaseRepository.update(
        xa,
        updateTransactionLastChangeBySql(appContextUserId, transactionId)
      )
      _ <- BaseRepository.batchUpdate[TransactionLineItemInsertSql](
        xa,
        insertTransactionLineItemsSql(),
        transactionCreateSql.lineItems.map(
          _.copy(transactionId = transactionId)
        )
      )
    } yield (transactionId)

    dbTrx.transact(xa)
  }

  def insertTransactionLineItem(
      transactionId: BigDecimal,
      categoryId: String,
      itemizedAmount: BigDecimal
  ): doobie.ConnectionIO[Long] = ???

  override def updateTransaction(
      transactionUpdateSql: TransactionUpdateSql
  ): doobie.ConnectionIO[Long] = ???

  override def deleteTransaction(id: Long): doobie.ConnectionIO[Long] = ???

  def createTransactionCategory(
      categoryCreateSql: TransactionCategoryCreateSql
  ): IO[Long] = {
    val dbTrx = for {
      categoryId <- BaseRepository.insertWithId(
        xa,
        createTransactionCategorySql(categoryCreateSql)
      )
    } yield (categoryId)

    dbTrx.transact(xa)
  }

  private def insertTransactionSql(
      transactionCreateSql: TransactionCreateSql
  ) = {
    sql"""
         INSERT INTO transaction(
         app_last_changed_by,
        original_insert,
         last_update,
         user_id,
         transaction_amount,
         transaction_date,
         location,
         recurring_transaction_id
         )
         values (
         -1,
         ${Instant.now()},
         ${Instant.now()},
         ${transactionCreateSql.userId},
         ${transactionCreateSql.transactionAmount},
         ${transactionCreateSql.transactionDate},
         ${transactionCreateSql.location},
         ${transactionCreateSql.recurringTransactionId}
         )
       """
  }

  private def insertTransactionLineItemsSql(): String =
    s"""
       |INSERT INTO transaction_line_item (
       |  app_last_changed_by,
       |  original_insert,
       |  last_update,
       |  transaction_id,
       |  transaction_category_id,
       |  itemized_amount
       |) VALUES (
       |  ?,
       |  ?,
       |  ?,
       |  ?,
       |  ?,
       |  ?
       |)
       |""".stripMargin

  private def updateTransactionLastChangeBySql(
      appContextUserId: Long,
      transactionId: Long
  ) = {
    sql"""
            UPDATE transaction
            SET app_last_changed_by = ${appContextUserId},
            last_update = ${Instant.now()}
            WHERE id = ${transactionId}
             """
  }

  private def updateTransactionLineItemsLastChangeBySql(
      appContextUserId: Long,
      transactionId: Long
  ) = {
    sql"""
            UPDATE transaction_line_item
            SET app_last_changed_by = ${appContextUserId},
            last_update = ${Instant.now()}
            WHERE id = ${transactionId}
             """
  }

  private def createTransactionCategorySql(
      categoryCreateSql: TransactionCategoryCreateSql
  ) = {
    sql"""
         INSERT INTO transaction_category(
         app_last_changed_by,
        original_insert,
         last_update,
         user_id,
         category_name)
         VALUES(
         ${categoryCreateSql.appLastChangedBy},
         ${Instant.now()},
         ${Instant.now()},
         ${categoryCreateSql.userId},
         ${categoryCreateSql.name}
         )
       """
  }

}
