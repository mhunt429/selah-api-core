package infrastructure.repository

import cats.effect.IO
import core.models.AppUser.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javatimedrivernative.*

import java.time.Instant

trait AppUserRepository {
  def createUser(createdUser: AppUserCreateRequest): IO[Long]
  def getUser(id: Long): IO[Option[AppUser]]

  def getUserByEmail(encryptedEmail: String): IO[Option[String]]
}
class AppUserRepositoryImpl(xa: Transactor[IO]) extends AppUserRepository {

  def createUser(createdUser: AppUserCreateRequest): IO[Long] = {
    BaseRepository
      .insertWithId(xa, createUserSql(createdUser))
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

  private def getUserQuery(id: Long) = {
    sql"""
         SELECT id,
         email,
         first_name,
         last_name,
         date_created FROM app_user WHERE id = $id"""
  }
  private def createUserSql(createdUser: AppUserCreateRequest) = {
    sql"""
        INSERT INTO app_user (
        email,
        password,
        first_name,
        last_name,
        date_created
        )
        VALUES (${createdUser.email},
        ${createdUser.password},
        ${createdUser.firstName},
        ${createdUser.lastName},
        ${createdUser.dateCreated})
      """
  }

  private def getUserByEmailSql(email: String) = {
    sql"""
         SELECT encrypted_email
         FROM app_user
         where encrypted_email = $email LIMIT 1
         """
  }
}
