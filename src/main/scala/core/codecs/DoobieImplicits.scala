package core.codecs

import core.models.AppUser.DataAccess.AppUser
import doobie.postgres.*
import doobie.{Meta, Read, Write}

import java.time.{Instant, LocalDateTime, ZonedDateTime}
import java.util.UUID

object DoobieImplicits {
  implicit val uuidMeta: Meta[UUID] =
    Meta[String].imap[UUID](UUID.fromString)(_.toString)

  implicit val appUserRead: Read[AppUser] =
    Read[
      (
          Long,
          Long,
          Long,
          Long,
          Long,
         Long,
          Array[Byte],
          String,
          String,
          Array[Byte],
          Array[Byte],
          Option[Long],
          Option[String],
          Option[Boolean],
          Option[Boolean]
      )
    ].map {
      case (
            originalInsertEpoch,
            lastUpdateEpoch,
            appLastChangedBy,
            id,
            accountId,
            createdEpoch,
            encryptedEmail,
            username,
            password,
            encryptedName,
            encryptedPhone,
            lastLoginEpoch,
            lastLoginIp,
            phoneVerified,
            emailVerified
          ) =>
        AppUser(
          originalInsertEpoch,
          lastUpdateEpoch,
          appLastChangedBy,
          id,
          accountId,
          createdEpoch,
          encryptedEmail,
          username,
          password,
          encryptedName,
          encryptedPhone,
          lastLoginEpoch,
          lastLoginIp,
          phoneVerified,
          emailVerified
        )
    }

  implicit val appUserWrite: Write[AppUser] =
    Write[
      (
          Long,
          Long,
          Long,
          Long,
          Long,
         Long,
          Array[Byte],
          String,
          String,
          Array[Byte],
          Array[Byte],
          Option[Long],
          Option[String],
          Option[Boolean],
          Option[Boolean]
      )
    ].contramap { user =>
      (
        user.originalInsertEpoch,
        user.lastUpdateEpoch,
        user.appLastChangedBy,
        user.id,
        user.accountId,
        user.createdEpoch,
        user.encryptedEmail,
        user.username,
        user.password,
        user.encryptedName,
        user.encryptedPhone,
        user.lastLoginEpoch,
        user.lastLoginIp,
        user.phoneVerified,
        user.emailVerified
      )
    }
}
