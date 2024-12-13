import sbt.*
import sbt.Keys.*
import sbt.librarymanagement.Resolver

lazy val root = project
  .in(file("."))
  .enablePlugins(RevolverPlugin)
  .settings(
    name := "selah-api",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    coverageEnabled := true,
    resolvers ++= Seq(
      Resolver.mavenCentral
    ),
    libraryDependencies ++= Seq(
      // Your existing dependencies
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.hashids" % "hashids" % hashIdsVersion,
      "org.tpolecat" %% "skunk-core" % skunkVersion,
      "co.fs2" %% "fs2-core" % fs2Version,
      "co.fs2" %% "fs2-io" % fs2Version,
      "org.tpolecat" %% "natchez-core" % "0.3.1",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-config" % "0.10.0",
      "is.cir" %% "ciris" % "3.2.0",
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.flywaydb" % "flyway-core" % flywayVersion,
      "org.log4s" %% "log4s" % "1.10.0",
      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test,
      "io.prometheus" % "simpleclient" % prometheusVersion,
      "io.prometheus" % "simpleclient_common" % prometheusVersion,
      "com.github.loki4j" % "loki-logback-appender" % "1.5.1",
      "com.auth0" % "java-jwt" % "4.4.0",
      "org.mindrot" % "jbcrypt" % bcryptVersion,
      "com.softwaremill.sttp.client3" %% "core" % sttpVersion,
      "com.softwaremill.sttp.client3" %% "circe" % sttpVersion,
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % sttpVersion
    )
  )

val scala3Version = "3.3.0"
val LogbackVersion = "1.4.7"
val catsEffectVersion = "3.5.0"
val circeVersion = "0.14.5"
val catsVersion = "2.9.0"
val http4sVersion = "0.23.21"
val hashIdsVersion = "1.0.3"
val skunkVersion = "0.6.0"
val fs2Version = "3.7.0"
val doobieVersion = "1.0.0-RC4"
val flywayVersion = "9.16.0"
val prometheusVersion = "0.16.0"
val bcryptVersion = "0.4"
val sttpVersion = "3.10.1"
