package library.management.system.com.config

import cats.syntax.all._
import library.management.system.com.config.Model.{ApiConfig, AppConfig, ConsumerConfig, DbConfig, KafkaConfig, ProducerConfig}
import ciris._

trait SettingsComponent {

  val config: Settings

  trait Settings {
    def appConfig: ConfigValue[Effect, AppConfig] = {
      for {
        api <- (
          env("API_PORT").or(prop("app.api.port")).as[Int],
          env("API_HOST").or(prop("app.api.host")).as[String]
        ).parMapN(ApiConfig.apply)
        database <- (
          env("DB_URL").or(prop("app.database.url")).as[String],
          env("DB_USER").or(prop("app.database.user")).as[String],
          env("DB_PASSWORD").or(prop("app.database.password")).as[String],
          env("DB_MAX_POOL_SIZE").or(prop("app.database.max-pool-size")).as[Int],
          env("DB_SCHEMA").or(prop("app.database.schema")).as[String]
        ).parMapN(DbConfig.apply)
        consumer <- (
          env("CONSUMER_SERVER").or(prop("app.queue.consumer.server")).as[String],
          env("CONSUMER_GROUP").or(prop("app.queue.consumer.group")).as[String],
          env("CONSUMER_TOPIC").or(prop("app.queue.consumer.topic")).as[String]
        ).parMapN(ConsumerConfig.apply)
        producer <- (
          env("PRODUCER_SERVER").or(prop("app.queue.producer.server")).as[String],
          env("PRODUCER_TOPIC").or(prop("app.queue.producer.topic")).as[String]
        ).parMapN(ProducerConfig.apply)
        queue = KafkaConfig(consumer, producer)
      } yield AppConfig(api, database, queue)
    }
  }
}
