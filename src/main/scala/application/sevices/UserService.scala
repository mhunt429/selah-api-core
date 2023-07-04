package application.sevices
import cats.effect.IO
import domain.models.AppUser.{AppUserCreate, AppUserViewModel}
import infrastructure.repository.AppUserRepository
import org.hashids.Hashids

trait UserService {
  def getUser(id: String): IO[Option[AppUserViewModel]]
  def createUser(user: AppUserCreate): IO[String]
}
class UserServiceImpl(
    appUserRepository: AppUserRepository,
    securityService: SecurityService
) extends UserService {

  def createUser(user: AppUserCreate): IO[String] = {
    for {
      userId <- appUserRepository.createUser(user)
      hashId <- securityService.encodeHashId(userId)
    } yield hashId
  }

  def getUser(id: String): IO[Option[AppUserViewModel]] = {
    for {
      decodedId <- securityService.decodeHashId(id)
      user <- appUserRepository.getUser(decodedId)
    } yield user
  }
}
