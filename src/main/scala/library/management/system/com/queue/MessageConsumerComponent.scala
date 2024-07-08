package library.management.system.com.queue

import fs2.kafka.{Deserializer, _}
import cats.effect.IO
import library.management.system.com.Main.logger
import library.management.system.com.db.components.ItemRepositoryComponent
import library.management.system.com.model.Exceptions.{BookManagementError, ConsumeError, DeserializationError}
import library.management.system.com.queue.Model.BookItemMessage
import org.typelevel.log4cats.Logger

trait MessageConsumerComponent {
  this: ItemRepositoryComponent =>

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

    def processExternalItems(commit: CommittableConsumerRecord[IO, String, Either[DeserializationError, BookItemMessage]]): IO[Either[BookManagementError, String]] =
      commit.record.value match {
        case Left(error) =>
          logger.error(s"Error while consuming message: ${error.errorMessage}!")
          IO.pure(Left(ConsumeError(error.error)))

        case Right(message) =>
          val bookItem = BookItemMessage.toBookItem(message)

          for {
            item <- itemRepository.createItem(bookItem)
            _ <- logger.info(s"Kafka external item with isbn ${bookItem.isbn} and tag ${bookItem.tag} was consumed with success!")
          } yield item
      }
  }
}
