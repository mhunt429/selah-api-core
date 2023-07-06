package domain.codecs
import doobie.Meta
import doobie.implicits.javasql.TimestampMeta
import doobie.postgres.*
import doobie.postgres.implicits.*

import java.sql.Timestamp
import java.time.{Instant, LocalDateTime, ZonedDateTime}
import java.util.UUID

object DoobieImplicits {
  implicit val uuidMeta: Meta[UUID] =
    Meta[String].imap[UUID](UUID.fromString)(_.toString)

  /*implicit val zonedDateTimeMeta: Meta[ZonedDateTime] =
    Meta[Timestamp].timap(timestamp => ZonedDateTime.from(timestamp.toInstant))(
      zonedDateTime => Timestamp.from(zonedDateTime.toInstant)
    )*/

  /*implicit val instantMeta: Meta[Instant] =
    Meta[Timestamp].timap(timestamp => timestamp.toInstant)(instant =>
      Timestamp.from(instant)
    )*/
}
