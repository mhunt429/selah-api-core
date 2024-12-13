package core.Plaid

import java.time.Instant
/*
Yes I know I am using snake case property names.
That is because I'd rather not have to encode/decode each property of a
class manually
 */

case class PlaidLinkToken(
    expiration: Instant,
    link_token: String,
    request_id: String
)

case class PlaidLinkTokenRequest(
    client_id: String,
    secret: String,
    country_codes: Seq[String] = Seq("US"),
    language: String = "en",
    products: Seq[String] = Seq("auth", "transactions"),
    client_name: String = "Selah",
    user: PlaidTokenUser
)

case class PlaidTokenUser(
    client_user_id: String
)

case class PlaidLinkTokenResponse(
                                   link_token: String,
                                 )
