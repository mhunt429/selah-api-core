package infrastructure.repository

import cats.effect.IO
import domain.models.AppUser.*
import doobie.*
import doobie.implicits.*
import fs2.Stream

import java.time.{Instant, ZoneId}

trait AppUserRepository {
  def createUser(createdUser: AppUserCreate): IO[Long]
  def getUser(id: Long): IO[Option[AppUser]]

  def getUserByEmail(email: String): IO[Option[String]]
}
class AppUserRepositoryImpl(xa: Transactor[IO]) extends AppUserRepository {
  private val logger = org.log4s.getLogger
  def createUser(createdUser: AppUserCreate): IO[Long] = {
    createUserSql(createdUser).update
      .withGeneratedKeys[Long]("id")
      .compile
      .lastOrError
      .transact(xa)
      .handleErrorWith(error => {
        logger.error(s"Postgres response with error => ${error.getMessage}")
        IO(0)
      })

  }

  def getUser(id: Long): IO[Option[AppUser]] = {
    getUserQuery(id)
      .query[AppUser]
      .option
      .transact(xa)
      .handleErrorWith(error => {
        logger.error(s"Postgres response with error => ${error.getMessage}")
        IO(None)
      })
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
  private def createUserSql(createdUser: AppUserCreate) = {
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
        ${Instant.now().toEpochMilli})
      """
  }

  private def getUserByEmailSql(email: String) = {
    /*
    ZonedDateTime dateTime = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.of("Australia/Sydney"));
     */
    println(
      Instant
        .ofEpochMilli(1695350509623L)
        .atZone(ZoneId.of("America/New_York"))
    )
    sql"""
         SELECT email
         FROM app_user
         where email = $email LIMIT 1
         """
  }

  //override def getUser(id: Int): IO[Option[AppUserViewModel]] = ???
}
