package infrastructure.repository
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import cats.effect.*
import scala.concurrent.ExecutionContext
trait HealthCheckRepository[F[_]] {

  //Get a processId from PostgreSQL as part of system health checks
  def getPostgresProcessId:F[Option[String]]
}

class HealthCheckRepositoryImpl[F[_]: Async](val xa: Transactor[F]) extends HealthCheckRepository[F]{
  def getPostgresProcessId:F[Option[String]] = {
     getPostgresProcessIdSql.query[String].option.transact(xa)
  }

  private def getPostgresProcessIdSql = {
    sql"""select pg_backend_pid()"""
  }
}