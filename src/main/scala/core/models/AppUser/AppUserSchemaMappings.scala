package core.models.AppUser

import doobie.postgres.implicits.*
import doobie.{Meta, Read}

import java.time.Instant

//Implicit types to serialize SQL Doobie queries to User Data Access Objects
object AppUserSchemaMappings {

  implicit val appUserRead: Read[AppUser] =
    Read[
      (
          Instant,
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

}
