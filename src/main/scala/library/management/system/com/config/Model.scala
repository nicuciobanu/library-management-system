package library.management.system.com.config

object Model {

  final case class AppConfig(api: ApiConfig, database: DbConfig, queue: KafkaConfig)

  case class ApiConfig(port: Int, host: String)

  case class DbConfig(url: String, user: String, password: String, maxPoolSize: Int, schema: String)

  case class KafkaConfig(consumer: ConsumerConfig, producer: ProducerConfig)
  case class ConsumerConfig(server: String, group: String, topic: String)
  case class ProducerConfig(server: String, topic: String)
}
