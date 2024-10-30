package infrastructure.repository

import cats.effect.IO
import core.models.Account.*
import doobie.Transactor
import doobie.implicits.*
import doobie.implicits.javatimedrivernative.*

import java.time.Instant
trait AccountRepository {
  def createAccount(accountName: String): IO[Long]
  def getAccount(id: Long): IO[Option[Account]]

  def updateAccount(id: Long, accountName: String): IO[Int]
}

class AccountRepositoryImpl(xa: Transactor[IO]) extends AccountRepository {

  def createAccount(accountName: String): IO[Long] = {
    for {
      id <- BaseRepository.insertWithId(xa, createAccountSql(accountName))
      _ <- updateAccountLastChangeBy(id)
    } yield id
  }

  def getAccount(id: Long): IO[Option[Account]] = {
    BaseRepository
      .get[Account](xa, getAccountSql(id))
  }

  def updateAccount(id: Long, accountName: String): IO[Int] = {
    BaseRepository.update(xa, updateAccountSql(id, accountName))
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
          account_name = ${accountName}
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
         date_created
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
