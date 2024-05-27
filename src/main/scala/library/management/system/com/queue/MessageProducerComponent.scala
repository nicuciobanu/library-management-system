package library.management.system.com.queue

import cats.effect._
import fs2._
import fs2.kafka._
import fs2.kafka.consumer.KafkaConsumeChunk.CommitNow

trait MessageProducerComponent {
  val messageProducer: MessageProducer
  trait MessageProducer {
    def produce(server: String): Stream[IO, KafkaProducer.PartitionsFor[IO, String, String]] = {
      val settings =
        ProducerSettings[IO, String, String]
          .withBootstrapServers(server)

      KafkaProducer.stream(settings)
    }

    def processRecords(
        producer: KafkaProducer[IO, String, String],
        topic: String
    )(records: Chunk[ConsumerRecord[String, String]]): IO[CommitNow] = {
      val producerRecords = records
        .map(consumerRecord => ProducerRecord(topic, consumerRecord.key, consumerRecord.value))

      producer.produce(producerRecords).flatten.as(CommitNow)
    }
  }
}
