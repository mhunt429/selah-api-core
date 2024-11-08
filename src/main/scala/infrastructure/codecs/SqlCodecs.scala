package infrastructure.codecs

import doobie.Meta
import doobie.implicits.*
import doobie.implicits.javasql.TimestampMeta

import java.sql.Timestamp
import java.time.Instant

object SqlCodecs {
  implicit val instantMeta: Meta[Instant] =
    Meta[Timestamp].imap(_.toInstant)(Timestamp.from)
}
