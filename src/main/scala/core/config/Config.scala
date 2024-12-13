package core.config

final case class Config(
    server: ServerConfig,
    db: DatabaseConfig,
    securityConfig: SecurityConfig,
    plaidConfig: PlaidConfig
)

final case class SecurityConfig(
    hashIdSalt: String,
    jwtSecret: String,
    cryptoSecret: String,
    accessTokenExpiryMinutes: Int,
    refreshTokenExpiryDays: Int
)

final case class PlaidConfig(
    plaidClientId: String,
    plaidClientSecret: String,
    plaidBaseUrl: String
)
