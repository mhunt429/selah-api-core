package infrastructure.repository
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import cats.effect.*
import scala.concurrent.ExecutionContext
trait HealthCheckRepository {

  //Get a processId from PostgreSQL as part of system health checks
  def getPostgresProcessId:IO[Option[String]]
}

class HealthCheckRepositoryImpl(val xa: Transactor[IO]) extends HealthCheckRepository{
  def getPostgresProcessId:IO[Option[String]] = {
     getPostgresProcessIdSql.query[String].option.transact(xa)
  }

  private def getPostgresProcessIdSql = {
    sql"""select pg_backend_pid()"""
  }
}