package application.sevices
import cats.effect.IO
import config.*
import org.hashids.Hashids

import scala.::
class SecurityService(hashService: Hashids) {

  def decodeHashId(id: String): IO[Long] = {
    //HashIds returns a list of ints but if bad id gets sent over, it will return a empty list
    //If that is that case, just convert that to an option and return 0 if the list is empty
    IO.pure(hashService.decode(id).toList.headOption.getOrElse(0L))
  }
  def encodeHashId(id: Long): IO[String] = IO(hashService.encode(id))
}
