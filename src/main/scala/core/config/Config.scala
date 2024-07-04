package core.config

final case class Config(
    server: ServerConfig,
    db: DatabaseConfig,
    securityConfig: SecurityConfig
)

final case class SecurityConfig(hashIdSalt: String, jwtSecret: String)
