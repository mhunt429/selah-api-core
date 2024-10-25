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
            Instant,
          Long,
          Long,
          Long,
            Instant,
          Array[Byte],
          String,
          String,
          Array[Byte],
          Array[Byte],
            Instant,
          Option[String],
          Option[Boolean],
          Option[Boolean]
      )
    ].map {
      case (
            originalInsert,
            lastUpdate,
            appLastChangedBy,
            id,
            accountId,
            createdDate,
            encryptedEmail,
            username,
            password,
            encryptedName,
            encryptedPhone,
            lastLogin,
            lastLoginIp,
            phoneVerified,
            emailVerified
          ) =>
        AppUser(
          originalInsert,
          lastUpdate,
          appLastChangedBy,
          id,
          accountId,
          createdDate,
          encryptedEmail,
          username,
          password,
          encryptedName,
          encryptedPhone,
          lastLogin,
          lastLoginIp,
          phoneVerified,
          emailVerified
        )
    }

}
