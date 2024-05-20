package library.management.system.com.config

object Model {

  final case class AppConfig(api: Api, database: Database)

  case class Api(port: Int, host: String)

  case class Database(url: String, user: String, password: String, maxPoolSize: Int, schema: String)
}
