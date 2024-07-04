package application.sevices
import org.hashids.Hashids
class SecurityService(hashService: Hashids) {

  def decodeHashId(id: String): Long = {
    //HashIds returns a list of ints but if bad id gets sent over, it will return a empty list
    //If that is that case, just convert that to an option and return 0 if the list is empty
    hashService.decode(id).toList.headOption.getOrElse(0L)
  }
  def encodeHashId(id: Long): String = hashService.encode(id)
}
