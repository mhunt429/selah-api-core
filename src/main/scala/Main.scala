import api.modules.{HttpApi, Repository, Services}
import cats.effect.*
import cats.effect.unsafe.IORuntime
import com.comcast.ip4s.{host, port}
import core.config.{Config, DatabaseConfig}
import core.json.ConfigJson.*
import doobie.ExecutionContexts
import io.circe.config.parser
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.CORS

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
      api = HttpApi.make(services, conf)
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
