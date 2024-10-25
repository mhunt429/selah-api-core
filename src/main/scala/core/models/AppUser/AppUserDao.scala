package core.models.AppUser

import java.time.Instant

//Cannonical model of the app_user table
case class AppUser(
    originalInsert: Instant,
    lastUpdate: Instant,
    appLastChangedBy: Long,
    id: Long,
    accountId: Long,
    createdDate: Instant,
    encryptedEmail: Array[Byte],
    username: String,
    password: String,
    encryptedName: Array[Byte],
    encryptedPhone: Array[Byte],
    lastLogin: Instant,
    lastLoginIp: Option[String],
    phoneVerified: Option[Boolean],
    emailVerified: Option[Boolean]
)

case class AppUserInsert(
    originalInsert: Instant = Instant.now(),
    lastUpdate: Instant,
    appLastChangedBy: Long,
    accountId: Long,
    createdDate: Instant = Instant.now(),
    encryptedEmail: Array[Byte],
    username: String,
    password: String,
    encryptedName: Array[Byte],
    encryptedPhone: Array[Byte],
    lastLogin: Instant = Instant.now(),
    lastLoginIp: Option[String],
    phoneVerified: Option[Boolean],
    emailVerified: Option[Boolean]
)

case class AppUserUpdate(
    encryptedEmail: Array[Byte],
    lastUpdate: Instant,
    username: String,
    encryptedName: Array[Byte],
    encryptedPhone: Array[Byte],
    phoneVerified: Option[Boolean],
    emailVerified: Option[Boolean]
) {
  def merge(originalRecord: AppUser): AppUser = {
    originalRecord.copy(
      username = username,
      encryptedEmail = encryptedEmail,
      encryptedName = encryptedName,
      encryptedPhone = encryptedPhone
    )
  }
}
