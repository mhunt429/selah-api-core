package core.models

case class PostgreSQL(ok: Boolean)

case class HealthCheck(PostgreSQL: PostgreSQL)
