package  modules

import cats.effect.Temporal
import cats.syntax.all._
import org.typelevel.log4cats.Logger

object Programs {
  def make[F[_]: Logger: Temporal](services: Services[F]): Programs[F] =
    new Programs[F](services ) {}
}

sealed abstract class Programs[F[_]:  Logger: Temporal] private (
  services: Services[F]
                                                                           
 ) {

}