package infrastructure.repository

import cats.effect.IO
import doobie.*
import doobie.implicits.*
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
object BaseRepository {
  def insertWithId(
      xa: Transactor[IO],
      fragment: Fragment
  ): IO[Long] = {
    fragment.update
      .withGeneratedKeys[Long]("id")
      .compile
      .lastOrError
      .transact(xa)
  }

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

  def update(
      xa: Transactor[IO],
      fragment: Fragment
  ): IO[Int] = {
    fragment.update.run
      .transact(xa)
  }

  def delete(
      xa: Transactor[IO],
      fragment: Fragment
  ): IO[Int] = {
    fragment.update.run
      .transact(xa)
  }
}
