import cats.Monad
import cats.effect.*
import config.*
import cats.effect.*
import doobie.ExecutionContexts
import io.circe.config.parser
import modules.*
import cats.implicits.*
import modules.Repository
import org.http4s.server.defaults.Banner
import scala.concurrent.ExecutionContext
import domain.json.ConfigJson.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import com.comcast.ip4s.{host,port}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    for{
      conf <- Resource.eval(parser.decodePathF[IO, Config]("appConfig"))
      serverEc <- ExecutionContexts.cachedThreadPool[IO]
      txnEc <- ExecutionContexts.cachedThreadPool[IO]
      xa <- DatabaseConfig.transactor(conf.db, txnEc)
      repo = Repository.make(xa)
      services = Services.make(repo)
      api = HttpApi.make[IO](services)
      server <- EmberServerBuilder.default[IO]
        .withHost(host"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(api.httpApp)
        .build
    }yield server
  }.use(server => IO.delay(println(s"Server Has Started at ${server.address}")) >>
    IO.never.as(ExitCode.Success))
}