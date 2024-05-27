package library.management.system.com.queue

import cats.effect.IO
import fs2.Chunk
import fs2.kafka._
import org.typelevel.log4cats.Logger
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.syntax.EncoderOps
import library.management.system.com.config.Model.KafkaConfig
import library.management.system.com.model.Exceptions._
import library.management.system.com.queue.Model.BookItemMessage
import library.management.system.com.queue.QueueCodec.deserializer

trait MessageServiceComponent {
  this: MessageConsumerComponent with MessageProducerComponent =>
  val queueService: MessageService
  trait MessageService {

    implicit val accountOperationDeserializer: Deserializer[IO, Either[DeserializationError, BookItemMessage]] =
      deserializer[BookItemMessage]

    def process(queue: KafkaConfig)(implicit logger: Logger[IO]): fs2.Stream[IO, Unit] =
      for {
        stream <- messageProducer
          .produce(queue.producer.server)
          .evalMap(producer =>
            messageConsumer
              .consume[BookItemMessage](queue.consumer.server, queue.consumer.group, queue.consumer.topic)
              .consumeChunk { chunk =>
                val commit = chunk.flatMap { record =>
                  val message: String =
                    record.value.fold(
                      err => {
                        logger.error(s"MessageServiceComponent :: while deserializing the message an error were thrown: $err!!!")
                        throw new Exception(err.errorMessage)
                      },
                      bookItemMsg => bookItemMsg.asJson.toString()
                    )

                  Chunk(
                    ConsumerRecord(
                      topic = record.topic,
                      partition = record.partition,
                      offset = record.offset,
                      key = record.key,
                      value = message
                    )
                  )
                }
                messageProducer.processRecords(producer, queue.producer.topic)(commit)
              }
              .attempt
              .flatMap {
                case Left(err) =>
                  logger.error(s"MessageServiceComponent :: while writing messages an error were thrown: $err!!!")
                  IO.raiseError(UnexpectedError(err))
                case Right(_) => IO.pure(())

              }
          )
      } yield stream
  }
}
