import cats.Monad
import cats.effect.*
import cats.effect.unsafe.IORuntime
import cats.implicits.*
import com.comcast.ip4s.{host, port}
import config.*
import domain.json.ConfigJson.*
import doobie.ExecutionContexts
import io.circe.config.parser
import modules.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.defaults.Banner
import org.http4s.server.middleware.{CORS, CORSPolicy}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext

object Main extends IOApp {
  private implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global
  def run(args: List[String]): IO[ExitCode] = {
    for {
      conf <- Resource.eval(parser.decodePathF[IO, Config]("appConfig"))
      serverEc <- ExecutionContexts.cachedThreadPool[IO]
      txnEc <- ExecutionContexts.cachedThreadPool[IO]
      xa <- DatabaseConfig.transactor(conf.db, txnEc)
      _ = DatabaseConfig.initialize(xa).unsafeRunSync()
      repo = Repository.make(xa)
      services = Services.make(repo, conf)
      api = HttpApi.make(services)
      server <- EmberServerBuilder
        .default[IO]
        .withHost(host"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(CORS.policy.withAllowOriginAll(api.httpApp))
        .build
    } yield server
  }.useForever
    .as(ExitCode.Success)
}
