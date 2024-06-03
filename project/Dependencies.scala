import sbt.*

object Dependencies {
  val DoobieVersion = "1.0.0-RC4"
  val CatsEffectVersion = "3.5.2"
  val Http4sVersion = "0.23.16"
  val ZioJson = "0.6.2"
  val CirceVersion = "0.14.7"
  val LoggerVersion = "1.4.12"

  val initialization: Seq[ModuleID] = Seq(
    // cats
    "org.typelevel"         %% "cats-effect"                % CatsEffectVersion,
    "is.cir"                %% "ciris"                      % "3.6.0",
    "org.flywaydb"           % "flyway-core"                % "10.4.1",
    "org.flywaydb"           % "flyway-database-postgresql" % "10.4.1" % "runtime",

    // doobie
    "org.tpolecat"          %% "doobie-core"                % DoobieVersion,
    "org.tpolecat"          %% "doobie-postgres"            % DoobieVersion,
    "org.tpolecat"          %% "doobie-hikari"              % DoobieVersion,

    // zio-json
    "dev.zio"               %% "zio-json"                   % ZioJson,
    "dev.zio"               %% "zio-json-interop-http4s"    % ZioJson,

    // circe
    "io.circe"              %% "circe-core"                 % CirceVersion,
    "io.circe"              %% "circe-generic"              % CirceVersion,
    "io.circe"              %% "circe-parser"               % CirceVersion,

    // http4s
    "org.http4s"            %% "http4s-blaze-server"        % Http4sVersion,
    "org.http4s"            %% "http4s-dsl"                 % Http4sVersion,

    // fs2-kafka
    "com.github.fd4s"       %% "fs2-kafka"                  % "3.5.1",

    // logging
    "ch.qos.logback"         % "logback-classic"            % LoggerVersion % Runtime
  )
}
