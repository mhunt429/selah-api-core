package core.security

case class CommonPII(
                      email: String,
                      password: String,
                      firstName: String,
                      lastName: String,
                      phone: String 
                    )

case class EncryptedPII(
                         encryptedEmail: String,
                         encryptedPassword: String,
                         encryptedName: String,
                         encryptedPhone: String
                       )


