package domain.HealthCheck
case class PostgreSQL(ok: Boolean)

case class HealthCheck(PostgreSQL: PostgreSQL)
