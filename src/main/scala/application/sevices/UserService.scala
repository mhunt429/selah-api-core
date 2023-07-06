package application.sevices
import cats.effect.IO
import cats.effect.unsafe.implicits.global
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
    val user = for {
      decodedId <- securityService.decodeHashId(id)
      user <- appUserRepository.getUser(decodedId)
    } yield (user)

    user.map {
      case Some(u) =>
        Some(
          AppUserViewModel(
            id = id,
            email = u.email,
            firstName = u.firstName,
            lastName = u.lastName,
            dateCreated = u.dateCreated
          )
        )
      case _ => None
    }
  }

}
