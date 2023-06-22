val scala3Version = "3.3.0"
val LogbackVersion = "1.4.7"
val catsEffectVersion = "3.5.0"
val circeVersion = "0.14.2"
val catsVersion = "2.9.0"
val http4sVersion = "0.23.21"
val log4catsVersion = "2.5.0"
lazy val root = project
  .in(file("."))
  .settings(
    name := "selah-api",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.typelevel" %% "log4cats-slf4j" % log4catsVersion
    )
  )
