package domain.HealthCheck
case class PostgresStatus(ok: Boolean)

case class HealthCheck(postgresqlStatus: PostgresStatus)