package api.middleware

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.{JWT, JWTVerifier}
import core.config.Config
import core.models.Application.AppRequestContext
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.server.AuthMiddleware
import org.http4s.util.CaseInsensitiveString

import java.util.UUID
object JwtMiddleware {
  def apply(config: Config): AuthMiddleware[IO, AppRequestContext] = {
    // Extract token from the request
    val tokenExtractor: Request[IO] => Either[String, String] = request => {
      val authToken = request.headers
        .get(CaseInsensitiveString("Authorization"))
        .flatMap { header =>
          if (header.exists(_.value.startsWith("Bearer ")))
            Some(header.map(_.value).head.substring(7)) // Strip "Bearer "
          else
            None
        }

      val cookieToken = request.cookies
        .find(_.name == "x_token")
        .map(_.content)

      //Check against the bearer token first and if null validate against the x_token header for client applications
      authToken.orElse(cookieToken) match {
        case Some(token) => Right(token)
        case None =>
          Left("Missing or invalid Authorization header and x_token cookie")
      }
    }
    // Validate the token and return AuthenticatedUser or error message
    val validateToken
        : Kleisli[IO, Request[IO], Either[String, AppRequestContext]] =
      Kleisli { request =>
        IO(tokenExtractor(request).flatMap { token =>
          verifyToken(token, config.securityConfig.jwtSecret, request.headers)
        })
      }

    // Handle auth failure (401 Unauthorized or Forbidden)
    val onFailure: AuthedRoutes[String, IO] = Kleisli { req =>
      OptionT.liftF(Forbidden(req.context)) // Lift response into IO context
    }

    // Create AuthMiddleware with validation and failure handling
    AuthMiddleware(validateToken, onFailure)
  }

  // Verifies JWT token and returns the username (or any payload)
  private def verifyToken(
      token: String,
      secret: String,
      headers: Headers
  ): Either[String, AppRequestContext] = {
    try {
      val algorithm = Algorithm.HMAC256(secret)
      val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer("selah-api")
        .build()

      val decodedJWT: DecodedJWT = verifier.verify(token)
      val appRequestContextId = decodedJWT.getClaim("sub").asString()
      val ipAddress = headers
        .get(CaseInsensitiveString("x-forwarded-for"))
        .map(_.head)

      Right(
        AppRequestContext(
          userId = appRequestContextId,
          ipAddress = ipAddress.map(_.value),
          requestId = UUID.randomUUID()
        )
      )
    } catch {
      case _: JWTVerificationException => Left("Invalid or expired token")
    }
  }
}
