package infrastructure.repository
import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
trait HealthCheckRepository {

  //Get a processId from PostgreSQL as part of system health checks
  def getPostgresProcessId: IO[Option[String]]
}

class HealthCheckRepositoryImpl(val xa: Transactor[IO])
    extends HealthCheckRepository {
  def getPostgresProcessId: IO[Option[String]] = {
    println(xa)
    getPostgresProcessIdSql.query[String].option.transact(xa)
  }

  private def getPostgresProcessIdSql = {
    sql"""select pg_backend_pid()"""
  }
}
