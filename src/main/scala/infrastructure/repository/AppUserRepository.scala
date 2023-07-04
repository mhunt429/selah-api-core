package infrastructure.repository

import cats.effect.IO
import domain.codecs.DoobieImplicits.*
import domain.models.AppUser.*
import doobie.*
import doobie.implicits.*
import fs2.Stream

import java.time.{Instant, ZonedDateTime}

trait AppUserRepository {
  def createUser(createdUser: AppUserCreate): IO[Long]
  def getUser(id: Long): IO[Option[AppUserViewModel]]
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

  def getUser(id: Long): IO[Option[AppUserViewModel]] = {
    getUserQuery(id)
      .query[AppUserViewModel]
      .option
      .transact(xa)
  }

  private def getUserQuery(id: Long) = {
    sql"""
         SELECT id,
         email,
         phone_number,
         first_name,
         last_name,
         date_created WHERE id = $id"""
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
        ${ZonedDateTime.now()})
      """
  }

  //override def getUser(id: Int): IO[Option[AppUserViewModel]] = ???
}
