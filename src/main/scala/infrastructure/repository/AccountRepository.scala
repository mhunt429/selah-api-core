package infrastructure.repository

import cats.effect.IO
import cats.implicits.*
import core.models.Account.*
import core.models.AppUser.AppUserInsert
import doobie.implicits.*
import doobie.implicits.javasql.TimestampMeta
import doobie.*

import java.sql.Timestamp
import java.time.Instant

trait AccountRepository {
  def createAccount(accountName: String, appUserInsert: AppUserInsert): IO[Long]
  def getAccount(id: Long): IO[Option[Account]]

  def updateAccount(id: Long, accountName: String): IO[Int]
}

class AccountRepositoryImpl(
    xa: Transactor[IO],
    appUserRepository: AppUserRepository
) extends AccountRepository {
  implicit val instantMeta: Meta[Instant] =
    Meta[Timestamp].imap(_.toInstant)(Timestamp.from)

  def createAccount(
      accountName: String,
      appUserInsert: AppUserInsert
  ): IO[Long] = {
    val transaction: ConnectionIO[Long] = for {
      id <- BaseRepository.insertWithId(xa, createAccountSql(accountName))
      _ <- updateAccountLastChangeBy(id)
      userId <- appUserRepository.createUser(appUserInsert.copy(accountId = id))
      _ <- appUserRepository.updateAccountLastChangeBy(userId)
    } yield id

    transaction.transact(xa)
  }

  def getAccount(id: Long): IO[Option[Account]] = {
    BaseRepository
      .get[Account](xa, getAccountSql(id))
  }

  def updateAccount(id: Long, accountName: String): IO[Int] = {
    BaseRepository
      .update(xa, updateAccountSql(id, accountName))
      .transact(xa)
  }

  private def getAccountSql(id: Long) = {
    sql"""
         SELECT * FROM account where id = ${id}
       """
  }
  private def updateAccountLastChangeBy(id: Long) = {
    BaseRepository.update(xa, updateAccountLastChangeBySql(id))
  }

  private def updateAccountSql(id: Long, accountName: String) = {
    sql"""
          UPDATE account
          SET app_last_changed_by = ${id},
          account_name = ${accountName},
          last_update = ${Instant.now()}
          WHERE id = ${id}
           """
  }
  private def updateAccountLastChangeBySql(id: Long) = {
    sql"""
        UPDATE account
        SET app_last_changed_by = ${id}
        WHERE id = ${id}
         """
  }

  private def createAccountSql(accountName: String) = {
    sql"""
         INSERT INTO account(
         original_insert,
         last_update,
         /*
         //Set app_last_changed_by to  -1 because this comes through the public endpoint
         and will be updated after the record in inserted with the newly created id
         */
         app_last_changed_by,
         date_created,
         account_name
         )
         VALUES (
         ${Instant.now()},
           ${Instant.now()},
           -1,
           ${Instant.now()},
           ${accountName}
         )
       """
  }

}
