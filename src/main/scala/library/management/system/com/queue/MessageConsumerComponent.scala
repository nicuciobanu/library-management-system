package library.management.system.com.queue

import fs2.kafka.{Deserializer, _}
import cats.effect.IO
import library.management.system.com.model.Exceptions.DeserializationError

trait MessageConsumerComponent {
  val messageConsumer: MessageConsumer
  trait MessageConsumer {
    def consume[Message](server: String, groupId: String, topic: String)(implicit
        deserializer: Deserializer[IO, Either[DeserializationError, Message]]
    ): fs2.Stream[IO, KafkaConsumer[IO, String, Either[DeserializationError, Message]]] = {
      val consumerSettings =
        ConsumerSettings[IO, String, Either[DeserializationError, Message]]
          .withAutoOffsetReset(AutoOffsetReset.Earliest)
          .withBootstrapServers(server)
          .withGroupId(groupId)

      KafkaConsumer
        .stream(consumerSettings)
        .subscribeTo(topic)
    }
  }
}
