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
      request.headers
        .get(CaseInsensitiveString("Authorization"))
        .headOption match {
        case Some(header)
            if header.exists(h => h.value.startsWith("Bearer ")) =>
          Right(header.map(_.value).head.substring(7)) // Strip "Bearer "
        case _ =>
          Left("Missing or invalid Authorization header")
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
