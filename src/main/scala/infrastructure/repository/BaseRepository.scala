package infrastructure.repository

import cats.effect.IO
import doobie.*
import doobie.implicits.*
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor

object BaseRepository {

//Any transactions should not auto-commit
//This is so we can chain writes, updates, and deletes together as needed
  def insertWithId(
      xa: Transactor[IO],
      fragment: Fragment
  ): ConnectionIO[Long] = {
    fragment.update
      .withGeneratedKeys[Long]("id")
      .compile
      .lastOrError
  }

  def insertWithIdAutoCommit(
      xa: Transactor[IO],
      fragment: Fragment
  ): IO[Long] = {
    fragment.update
      .withGeneratedKeys[Long]("id")
      .compile
      .lastOrError
      .transact(xa)
  }

  def update(
      xa: Transactor[IO],
      fragment: Fragment
  ): ConnectionIO[Int] = {
    fragment.update.run
  }

  def batchUpdate[A: Write](
      xa: Transactor[IO],
      sql: String,
      values: Seq[A]
  ): ConnectionIO[Int] = {
    val update = Update[A](sql)
    update.updateMany(values)
  }

  def delete(
      xa: Transactor[IO],
      fragment: Fragment
  ): ConnectionIO[Int] = {
    fragment.update.run
  }

  // Reads can return an IO instead of ConnectionIO

  def getAll[A](xa: Transactor[IO], fragment: Fragment)(implicit
      meta: Read[A]
  ): IO[List[A]] = {
    fragment
      .query[A]
      .stream
      .compile
      .toList
      .transact(xa)
  }

  def get[A](xa: Transactor[IO], fragment: Fragment)(implicit
      meta: Read[A]
  ): IO[Option[A]] = {
    fragment
      .query[A]
      .option
      .transact(xa)
  }

}
