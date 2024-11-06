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
    encryptedEmail: String,
    username: String,
    password: String,
    encryptedName: String,
    encryptedPhone: String,
    lastLogin: Instant,
    lastLoginIp: Option[String],
    phoneVerified: Option[Boolean],
    emailVerified: Option[Boolean]
)

case class AppUserInsert(
    originalInsert: Instant = Instant.now(),
    lastUpdate: Instant = Instant.now(),
    appLastChangedBy: Long,
    accountId: Long,
    createdDate: Instant = Instant.now(),
    encryptedEmail: String,
    username: String,
    password: String,
    encryptedName: String,
    encryptedPhone: String,
    lastLogin: Instant = Instant.now(),
    lastLoginIp: Option[String],
    phoneVerified: Option[Boolean],
    emailVerified: Option[Boolean]
)

case class AppUserUpdate(
    encryptedEmail: String,
    lastUpdate: Instant,
    username: String,
    encryptedName: String,
    encryptedPhone: String,
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
