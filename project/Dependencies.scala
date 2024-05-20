import sbt.*

object Dependencies {
  val DoobieVersion = "1.0.0-RC4"
  val CatsEffectVersion = "3.5.2"
  val Http4sVersion = "0.23.16"
  val ZioJson = "0.6.2"

  val initialization: Seq[ModuleID] = Seq(
    // cats
    "org.typelevel"         %% "cats-effect" % CatsEffectVersion,
    "is.cir"                %% "ciris"       % "3.6.0",
    "org.flywaydb"           % "flyway-core" % "10.4.1",
    "org.flywaydb"           % "flyway-database-postgresql" % "10.4.1" % "runtime",

    // doobie
    "org.tpolecat" %% "doobie-core" % DoobieVersion,
    "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
    "org.tpolecat" %% "doobie-hikari" % DoobieVersion,

    // zio-json
    "dev.zio"      %% "zio-json" % ZioJson,

    // https
    "org.http4s"            %% "http4s-blaze-server"    % Http4sVersion,
    "org.http4s"            %% "http4s-dsl"             % Http4sVersion,
    "org.typelevel"         %% "log4cats-slf4j"         % "2.7.0",
    "dev.zio"               %% "zio-json-interop-http4s" % ZioJson
  )
}
