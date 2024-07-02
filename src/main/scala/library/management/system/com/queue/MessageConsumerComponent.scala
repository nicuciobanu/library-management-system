package library.management.system.com.queue

import fs2.kafka.{Deserializer, _}
import cats.effect.IO
import library.management.system.com.config.Model.ConsumerConfig
import library.management.system.com.model.Exceptions.{ConsumeError, DeserializationError}
import org.typelevel.log4cats.Logger

trait MessageConsumerComponent {
  val messageConsumer: MessageConsumer
  trait MessageConsumer {
    def consume[Message](server: String, group: String, topic: String)(implicit
        deserializer: Deserializer[IO, Either[DeserializationError, Message]],
        logger: Logger[IO]
    ): fs2.Stream[IO, KafkaConsumer[IO, String, Either[DeserializationError, Message]]] = {
      val consumerSettings =
        ConsumerSettings[IO, String, Either[DeserializationError, Message]]
          .withAutoOffsetReset(AutoOffsetReset.Earliest)
          .withBootstrapServers(server)
          .withGroupId(group)

      KafkaConsumer
        .stream(consumerSettings)
        .subscribeTo(topic)
    }
  }
}
