package library.management.system.com.config

import cats.syntax.all._
import library.management.system.com.config.Model.{Api, AppConfig, Database}
import ciris._

trait SettingsComponent {

  val config: Settings

  trait Settings {
    def appConfig: ConfigValue[Effect, AppConfig] = {
      for {
        api <- (
          env("API_PORT").or(prop("app.api.port")).as[Int],
          env("API_HOST").or(prop("app.api.host")).as[String],
        ).parMapN(Api.apply)
        database <- (
          env("DB_URL").or(prop("app.database.url")).as[String],
          env("DB_USER").or(prop("app.database.user")).as[String],
          env("DB_PASSWORD").or(prop("app.database.password")).as[String],
          env("DB_MAX_POOL_SIZE").or(prop("app.database.max-pool-size")).as[Int],
          env("DB_SCHEMA").or(prop("app.database.schema")).as[String]
        ).parMapN(Database.apply)
      } yield AppConfig(api, database)
    }



  }
}
