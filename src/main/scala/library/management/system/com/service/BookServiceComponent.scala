package library.management.system.com.service

import cats.effect.IO
import fs2.kafka.ProducerResult
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.syntax.EncoderOps
import library.management.system.com.config.SettingsComponent
import library.management.system.com.db.components.{BookRepositoryComponent, ItemRepositoryComponent}
import library.management.system.com.model.Exceptions._
import library.management.system.com.model.Model.{Book, BookItem}
import library.management.system.com.queue.MessageProducerComponent
import library.management.system.com.queue.Model.BookItemMessage
import library.management.system.com.util.Utils.toUUID
import org.typelevel.log4cats.Logger

import java.util.UUID

trait BookServiceComponent {
  this: BookRepositoryComponent with ItemRepositoryComponent with MessageProducerComponent with SettingsComponent =>

  val bookService: BookService

  trait BookService {
    def createBook(book: Book)(implicit logger: Logger[IO]): IO[Either[CreateError, String]] =
      bookRepository.createBook(book)

    def getBook(isbn: String, subject: String)(implicit logger: Logger[IO]): IO[Either[SearchError, Book]] =
      bookRepository.getBook(isbn, subject)

    def createBookItem(item: BookItem)(implicit logger: Logger[IO]): IO[Either[CreateError, String]] =
      for {
        _      <- bookRepository.getBook(item.isbn, item.subject)
        result <- itemRepository.createItem(item)
        _      <- publishMessage(item)
      } yield result

    def getBookItem(barcode: String, tag: String)(implicit logger: Logger[IO]): IO[Either[SearchError, BookItem]] = {
      val tagUUID = toUUID(tag)
      itemRepository.getItem(barcode, tagUUID)
    }

    private def publishMessage(item: BookItem)(implicit logger: Logger[IO]): IO[Either[PublishError, ProducerResult[String, String]]] =
      for {
        producerConfig <- config.appConfig.load[IO].map(_.queue.producer)
        msg = BookItemMessage.fromBookItem(item)
        result <- messageProducer.produce(msg.isbn, msg.asJson.toString(), producerConfig.server, producerConfig.topic)
      } yield result

    private def publishMessageToExternalTopic(
        item: BookItem
    )(implicit logger: Logger[IO]): IO[Either[PublishError, ProducerResult[String, String]]] =
      for { // we will use this method instead of adding manually messages to the external topic
        consumerConfig <- config.appConfig.load[IO].map(_.queue.consumer)
        barcode = UUID.randomUUID().toString
        tag     = UUID.randomUUID()
        msg     = BookItemMessage.fromBookItem(item).copy(barcode = barcode, tag = tag)
        result <- messageProducer.produce(msg.isbn, msg.asJson.toString(), consumerConfig.server, consumerConfig.topic)
      } yield result
  }
}
