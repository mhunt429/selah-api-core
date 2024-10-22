package core.models.AppUser

import java.time.Instant

case class AppUser(
    originalInsertEpoch: Instant,
    lastUpdateEpoch: Long,
    appLastChangedBy: Long,
    id: Long,
    accountId: Long,
    createdEpoch: Long,
    encryptedEmail: Array[Byte],
    username: String,
    password: String,
    encryptedName: Array[Byte],
    encryptedPhone: Array[Byte],
    lastLoginEpoch: Option[Long],
    lastLoginIp: Option[String],
    phoneVerified: Option[Boolean],
    emailVerified: Option[Boolean]
)

case class AppUserUpdate(
    encryptedEmail: Array[Byte],
    lastUpdateEpoch: Long = Instant.now().toEpochMilli,
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
