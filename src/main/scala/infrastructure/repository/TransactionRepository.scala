package infrastructure.repository
import cats.effect.IO
import core.transactions.sql.codecs.*
import core.transactions.sql.{TransactionCreateSql, TransactionLineItemInsertSql, TransactionUpdateSql}
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.TimestampMeta

import java.sql.Timestamp
import java.time.Instant

trait TransactionRepository {
  def insertTransaction(
      transactionCreateSql: TransactionCreateSql
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
}

class TransactionRepositoryImpl(xa: Transactor[IO])
    extends TransactionRepository {

  implicit val instantMeta: Meta[Instant] =
    Meta[Timestamp].imap(_.toInstant)(Timestamp.from)

  override def insertTransaction(
      transactionCreateSql: TransactionCreateSql
  ): IO[Long] = {
    //Begin by creating a database transaction
    val dbTrx: ConnectionIO[Long] = for {
      transactionId <- BaseRepository.insertWithId(
        xa,
        insertTransactionSql(transactionCreateSql)
      )
      _ <- BaseRepository.batchUpdate[TransactionLineItemInsertSql](
        xa,
        insertTransactionLineItemsSql(),
        transactionCreateSql.lineItems
      )
    } yield (transactionId)

    dbTrx.transact(xa)
  }

  override def insertTransactionLineItem(
      transactionId: BigDecimal,
      categoryId: String,
      itemizedAmount: BigDecimal
  ): doobie.ConnectionIO[Long] = ???

  override def updateTransaction(
      transactionUpdateSql: TransactionUpdateSql
  ): doobie.ConnectionIO[Long] = ???

  override def deleteTransaction(id: Long): doobie.ConnectionIO[Long] = ???

  private def insertTransactionSql(
      transactionCreateSql: TransactionCreateSql
  ) = {
    sql"""
         INSERT INTO transaction(
         app_last_changed_by,
        original_insert,
         last_update,
         user_id,
         account_id,
         transaction_amount,
         transaction_date,
         location,
         pending,
         recurring_transaction_id
         )
         values (
         -1,
         ${Instant.now()},
         ${Instant.now()},
         ${transactionCreateSql.userId},
         ${transactionCreateSql.accountId},
         ${transactionCreateSql.transactionDate},
         ${transactionCreateSql.location},
         ${transactionCreateSql.pending},
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
       |  category_id,
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

  private def updateTransactionLastChangeBySql(id: Long) = {
    sql"""
            UPDATE transaction
            SET app_last_changed_by = ${id},
            last_update = ${Instant.now()}
            WHERE id = ${id}
             """
  }

  private def updateTransactionLineItemsLastChangeBySql(id: Long) = {
    sql"""
            UPDATE transaction_line_item
            SET app_last_changed_by = ${id},
            last_update = ${Instant.now()}
            WHERE id = ${id}
             """
  }
}
