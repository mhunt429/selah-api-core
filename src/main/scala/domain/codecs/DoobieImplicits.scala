package domain.codecs
import domain.models.AppUser.AppUser
import doobie.postgres.*
import doobie.{Meta, Read, Write}

import java.time.{Instant, LocalDateTime, ZonedDateTime}
import java.util.UUID

object DoobieImplicits {
  implicit val uuidMeta: Meta[UUID] =
    Meta[String].imap[UUID](UUID.fromString)(_.toString)

  implicit val appUserRead: Read[AppUser] =
    Read[(Long, String, String, String, Long)].map {
      case (id, email, firstName, lastName, dateCreated) =>
        AppUser(
          id,
          email,
          firstName,
          lastName,
          dateCreated
        )
    }

  implicit val appUserWrite: Write[AppUser] =
    Write[(Long, String, String, String, Long)].contramap(user =>
      (user.id, user.email, user.firstName, user.lastName, user.dateCreated)
    )
  /*implicit val zonedDateTimeMeta: Meta[ZonedDateTime] =
    Meta[Timestamp].timap(timestamp => ZonedDateTime.from(timestamp.toInstant))(
      zonedDateTime => Timestamp.from(zonedDateTime.toInstant)
    )*/

  /*implicit val instantMeta: Meta[Instant] =
    Meta[Timestamp].timap(timestamp => timestamp.toInstant)(instant =>
      Timestamp.from(instant)
    )*/
}
