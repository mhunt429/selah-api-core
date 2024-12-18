package utils

import core.Plaid.{PlaidLinkToken, PlaidLinkTokenRequest, PlaidLinkTokenResponse, PlaidTokenUser}
import core.config.*
import core.identity.{AccessTokenResponse, LoginRequest, LoginResponse}
import core.json.BaseJson.*
import core.models.Account.AccountCreateResponse
import core.models.AppUser.sql.AppUserUpdate
import core.models.AppUser.{AppUserCreateRequest, AppUserViewModel}
import core.models.Registration.RegistrationHttpRequest
import core.models.{HealthCheck, PostgreSQL}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

trait Protocol {

  implicit val appUserViewModelEncoder: Encoder[AppUserViewModel] =
    deriveEncoder[AppUserViewModel]
  implicit val appUserViewModelDecoder: Decoder[AppUserViewModel] =
    deriveDecoder[AppUserViewModel]

  // Encoder and Decoder for AppUserCreate
  implicit val appUserCreateEncoder: Encoder[AppUserCreateRequest] =
    deriveEncoder[AppUserCreateRequest]
  implicit val appUserCreateDecoder: Decoder[AppUserCreateRequest] =
    deriveDecoder[AppUserCreateRequest]

  // Encoder and Decoder for AppUserUpdate
  implicit val appUserUpdateEncoder: Encoder[AppUserUpdate] =
    deriveEncoder[AppUserUpdate]
  implicit val appUserUpdateDecoder: Decoder[AppUserUpdate] =
    deriveDecoder[AppUserUpdate]

  implicit val accountCreateEncoder: Encoder[RegistrationHttpRequest] =
    deriveEncoder[RegistrationHttpRequest]
  implicit val accountCreateDecoder: Decoder[RegistrationHttpRequest] =
    deriveDecoder[RegistrationHttpRequest]

  implicit val accountCreateResponseEncoder: Encoder[AccountCreateResponse] =
    deriveEncoder[AccountCreateResponse]
  implicit val accountCreateResponseDecoder: Decoder[AccountCreateResponse] =
    deriveDecoder[AccountCreateResponse]

  implicit val loginRequestEncoder: Encoder[LoginRequest] =
    deriveEncoder[LoginRequest]

  implicit val loginRequestDecoder: Decoder[LoginRequest] =
    deriveDecoder[LoginRequest]

  implicit val accessTokenResponseEncoder: Encoder[AccessTokenResponse] =
    deriveEncoder[AccessTokenResponse]
  implicit val accessTokenResponseDecoder: Decoder[AccessTokenResponse] =
    deriveDecoder[AccessTokenResponse]

  implicit val loginResponseEncoder: Encoder[LoginResponse] =
    deriveEncoder[LoginResponse]
  implicit val loginResponseDecoder: Decoder[LoginResponse] =
    deriveDecoder[LoginResponse]

  implicit val postgresHealthEncoder: Encoder[PostgreSQL] =
    deriveEncoder[PostgreSQL]
  implicit val postgresHealthDecoder: Decoder[PostgreSQL] =
    deriveDecoder[PostgreSQL]

  implicit val healthCheckEncoder: Encoder[HealthCheck] =
    deriveEncoder[HealthCheck]
  implicit val healthCheckDecoder: Decoder[HealthCheck] =
    deriveDecoder[HealthCheck]
  implicit val serverEncoder: Encoder[ServerConfig] =
    deriveEncoder[ServerConfig]
  implicit val serverDecoder: Decoder[ServerConfig] =
    deriveDecoder[ServerConfig]
  implicit val dataBaseEncoder: Encoder[DatabaseConfig] =
    deriveEncoder[DatabaseConfig]
  implicit val databaseDecoder: Decoder[DatabaseConfig] =
    deriveDecoder[DatabaseConfig]
  implicit val plaidEncoder: Encoder[PlaidConfig] = deriveEncoder[PlaidConfig]
  implicit val plaidDecoder: Decoder[PlaidConfig] = deriveDecoder[PlaidConfig]

  implicit val securityEncoder: Encoder[SecurityConfig] =
    deriveEncoder[SecurityConfig]
  implicit val securityDecoder: Decoder[SecurityConfig] =
    deriveDecoder[SecurityConfig]

  implicit val encoder: Encoder[Config] = deriveEncoder[Config]
  implicit val decoder: Decoder[Config] = deriveDecoder[Config]

  implicit val plaidTokenUserDecoder: Decoder[PlaidTokenUser] =
    deriveDecoder[PlaidTokenUser]
  implicit val plaidTokenUserEncoder: Encoder[PlaidTokenUser] =
    deriveEncoder[PlaidTokenUser]
  implicit val plaidTokenLinkReqDecoder: Decoder[PlaidLinkTokenRequest] =
    deriveDecoder[PlaidLinkTokenRequest]
  implicit val plaidTokenLinkReqEncoder: Encoder[PlaidLinkTokenRequest] =
    deriveEncoder[PlaidLinkTokenRequest]
  implicit val plaidTokenLinkRspDecoder: Decoder[PlaidLinkToken] =
    deriveDecoder[PlaidLinkToken]
  implicit val plaidTokenLinkRspEncoder: Encoder[PlaidLinkToken] =
    deriveEncoder[PlaidLinkToken]

  implicit val plaidTokenLinkViewModelDecoder: Decoder[PlaidLinkTokenResponse] =
    deriveDecoder[PlaidLinkTokenResponse]

  implicit val plaidTokenLinkViewModelEncoder: Encoder[PlaidLinkTokenResponse] =
    deriveEncoder[PlaidLinkTokenResponse]

}
