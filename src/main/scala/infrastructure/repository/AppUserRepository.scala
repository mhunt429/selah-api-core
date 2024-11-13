package infrastructure.repository

import cats.effect.IO
import core.models.AppUser.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.TimestampMeta

import java.sql.Timestamp
import java.time.Instant

trait AppUserRepository {
  def createUser(createdUser: AppUserInsert): ConnectionIO[Long]

  def createUserWithCommit(createdUser: AppUserInsert): IO[Long]

  def getUser(id: Long): IO[Option[AppUser]]

  def updateAccountLastChangeBy(id: Long): ConnectionIO[Int]

  def getUserByEmail(encryptedEmail: String): IO[Option[String]]
}

class AppUserRepositoryImpl(xa: Transactor[IO]) extends AppUserRepository {
  implicit val instantMeta: Meta[Instant] =
    Meta[Timestamp].imap(_.toInstant)(Timestamp.from)

  def createUser(createdUser: AppUserInsert): ConnectionIO[Long] = {
    BaseRepository
      .insertWithId(xa, createUserSql(createdUser))
  }

  def createUserWithCommit(createdUser: AppUserInsert): IO[Long] = {
    BaseRepository
      .insertWithId(xa, createUserSql(createdUser))
      .transact(xa)
  }

  def getUser(id: Long): IO[Option[AppUser]] = {
    BaseRepository
      .get[AppUser](xa, getUserQuery(id))
  }

  def getUserByEmail(email: String): IO[Option[String]] = {
    getUserByEmailSql(email)
      .query[String]
      .option
      .transact(xa)
  }

  def updateAccountLastChangeBy(id: Long): ConnectionIO[Int] = {
    BaseRepository.update(xa, updateUserLastChangeBySql(id))
  }

  private def getUserQuery(id: Long) = {
    sql"""
         SELECT * from app_user WHERE id = $id"""
  }
  private def createUserSql(createdUser: AppUserInsert) = {
    sql"""
        INSERT INTO app_user (
        original_insert,
        last_update,
        app_last_changed_by,
        account_id,
        created_date,
        encrypted_email,
        username,
        password,
        encrypted_name,
        encrypted_phone,
        last_login
        )
        VALUES (${createdUser.originalInsert},
        ${createdUser.lastUpdate},
        /*set to -1 temporarily because we will update it with the auto-incrementing primary key*/
        -1,
        ${createdUser.accountId},
        ${createdUser.createdDate},
        ${createdUser.encryptedEmail},
        ${createdUser.username},
        ${createdUser.password},
        ${createdUser.encryptedName},
        ${createdUser.encryptedPhone},
        ${Instant.now()}
         )
      """
  }

  private def getUserByEmailSql(email: String) = {
    sql"""
         SELECT encrypted_email
         FROM app_user
         where encrypted_email = $email LIMIT 1
         """
  }

  private def updateUserLastChangeBySql(id: Long) = {
    sql"""
        UPDATE app_user
        SET app_last_changed_by = ${id},
        last_update = ${Instant.now()}
        WHERE id = ${id}
         """
  }
}
