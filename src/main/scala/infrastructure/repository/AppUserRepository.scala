package infrastructure.repository

import cats.effect.IO
import domain.Meta.DoobieImplicits.*
import domain.models.AppUser.*
import doobie.*
import doobie.implicits.*
import fs2.Stream

trait AppUserRepository {
  def createUser(createdUser: AppUserCreate): IO[Long]
  //def getUser(id: Int): IO[Option[AppUserViewModel]]
}
class AppUserRepositoryImpl(xa: Transactor[IO]) extends AppUserRepository {
  def createUser(createdUser: AppUserCreate): IO[Long] = {
    createUserSql(createdUser).update
      .withGeneratedKeys[Long]("id")
      .compile
      .lastOrError
      .transact(xa)

  }

  private def createUserSql(createdUser: AppUserCreate) = {
    sql"""
        INSERT INTO app_user (
        email,
        password,
        password_confirmation,
        phone_number,
        first_name,
        last_name,
        date_created,
        date_created_utc)
        VALUES (${createdUser.email},
        ${createdUser.password},
        ${createdUser.passwordConfirmation},
        ${createdUser.phoneNumber},
        ${createdUser.firstName},
        ${createdUser.lastName},
        ${createdUser.dateCreated},
        ${createdUser.dateCreatedUtc})
      """
  }

  //override def getUser(id: Int): IO[Option[AppUserViewModel]] = ???
}
