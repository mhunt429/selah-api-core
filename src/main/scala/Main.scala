import cats.effect.*
import config.*
import cats.effect.*
import doobie.ExecutionContexts
import io.circe.config.parser
import modules._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    for{
      conf <- Resource.eval(parser.decodePathF[IO, Config]("appConfig"))
      serverEc <- ExecutionContexts.cachedThreadPool[IO]
      connEc <- ExecutionContexts.fixedThreadPool[IO](conf.db.connections.poolSize)
      txnEc <- ExecutionContexts.cachedThreadPool[IO]
      xa <- DatabaseConfig.dbTransactor(conf.db, txnEc)
      repo <- Repository.make(xa)
      services <- Services.make[IO](repo)
    }yield()
    }
  }


