package utils

import cats.effect.unsafe.IORuntime
import cats.effect.{IO, Resource}
import core.config.{Config, DatabaseConfig, SecurityConfig, ServerConfig}
import doobie.ExecutionContexts
import doobie.util.transactor.Transactor

object TestHelpers {
  private implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global
  //test configuration for unit/integration tests
  val testConfig: Config = Config(
    server = ServerConfig(host = "localhost", port = 8080),
    db = DatabaseConfig(
      url = "jdbc:postgresql://localhost:55432/postgres",
      driver = "org.postgresql.Driver",
      user = "postgres",
      password = "postgres",
      connections = 10
    ),
    securityConfig = SecurityConfig(
      hashIdSalt =
        "nrpUQwpE+apuzGtJCbLPLP8RrVjtdWMDNOY0lY3caePQELClIL4KiHOYI4jSxolsYunXkgAGypX16yxtHLKgPNCxN46gBf3jy/DwhEsfz0gKOca1iHkDQwJBSdUGgmzfYXs96sIlOQW+uauOOEFTaVWatSs++TX3e2dn0qnqpdvr0CkHLeE+CcPROvzJ3Ue1zLj7yy9h6NgCZu2sTjl56F7eERSmlsH5igAKkB2GkKlOzQ3hFLx8Ht0gE3Ami24UCiueIPXN+Ttq+kImQXzni9Kb9tbgbQvOvmWa/eKx332Cg72+Yj4NAq1fNRmtTeZrgnEE29sw2VP20W2jDKBxlA==",
      jwtSecret =
        "szDAEw0HrY3Z5aYnKMA4WZxbvtqAeOcTsWmQub5MzYEUCVM+1mq/fHQD40amI5VFrwmoqiRxqSnbQF32IkAdMk3QtMIV/QvZSzZ6TEZJCJIDXpHRnFBpccV1gZwN8xY82x8VZ3tkcHnlYbqZ4D8Xki8Cyu/so9cNPYyWLqQDPncjvW3gF0E5xhoPytOBMsmGeMC3qscUXVP38nImeOmDBGPzWKdWTSqJcEIyoLcExFaFCHOt5M3zJcB1o1HAp9yodOnbBurXuNXSspmMVTOF7eICMHPQQfP6FZdFuf+4Z4BmCsrne0KqOnpWg4lgpU8GnV2RyrefCiwCXty1gA3tag==",
      cryptoSecret =
        "3ca5a1185b613cee4ba947839490427c36f1994809cb9a603c2a3717dd39d475",
      accessTokenExpiryMinutes = 60,
      refreshTokenExpiryDays = 30
    )
  )

  def initializeTestDb(): Resource[IO, Transactor[IO]] = {
    for {
      serverEc <- ExecutionContexts.cachedThreadPool[IO]
      txnEc <- ExecutionContexts.cachedThreadPool[IO]
      xa <- DatabaseConfig.transactor(testConfig.db, txnEc)
      _ <- Resource.eval(DatabaseConfig.cleanSchema(xa)) // Ensure schema is cleaned
    } yield xa
  }

}
