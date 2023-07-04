package application.sevices
import cats.effect.IO
import config.*
import org.hashids.Hashids
class SecurityService(hashService: Hashids) {

  def decodeHashId(id: String): IO[Long] = IO.pure(hashService.decode(id).head)
  def encodeHashId(id: Long): IO[String] = IO(hashService.encode(id))
}
