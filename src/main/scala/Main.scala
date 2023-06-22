import cats.effect.*
import cats.effect.syntax
import com.comcast.ip4s.{host, port}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
object Main extends  IOApp{
  def run(args: List[String]): IO[ExitCode] = {
    val host = host"0.0.0.0"
    val port = port"8080"
    for{
      server <- EmberServerBuilder.default[IO]
        .withHost(host)
        .withPort(port)
        .build
    }yield server
  }.use(server => IO.delay(println(s"Server Has Started at ${server.address}")) >>
    IO.never.as(ExitCode.Success))
}

