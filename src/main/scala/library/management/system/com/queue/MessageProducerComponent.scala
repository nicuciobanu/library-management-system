package library.management.system.com.queue

import cats.effect._
import fs2.kafka._
import library.management.system.com.config.Model.ProducerConfig
import library.management.system.com.model.Exceptions.PublishError
import org.typelevel.log4cats.Logger

trait MessageProducerComponent {
  val messageProducer: MessageProducer
  trait MessageProducer {
    def produce(key: String, payload: String)(
        config: ProducerConfig
    )(implicit logger: Logger[IO]): IO[Either[PublishError, ProducerResult[String, String]]] = {
      val settings =
        ProducerSettings[IO, String, String]
          .withBootstrapServers(config.server)
          .withProperty("topic.creation.enable", "true")

      KafkaProducer
        .resource(settings)
        .use(_.produceOne(ProducerRecord(config.topic, key, payload).withHeaders(Headers(Header[String]("correlation_id", key)))).flatten)
        .attempt
        .flatMap {
          case Left(error) =>
            logger.error(s"Error while publishing message: ${error.getMessage}!").toResource
            IO.pure(Left(PublishError(error)))
          case Right(record) =>
            logger.info(s"Message was published with success!").toResource
            IO.pure(Right(record))
        }
    }
  }
}
