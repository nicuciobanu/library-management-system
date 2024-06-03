package library.management.system.com.db.components

import cats.effect.IO
import fs2.kafka.{Deserializer, ProducerResult}
import library.management.system.com.config.SettingsComponent
import library.management.system.com.model.Exceptions._
import library.management.system.com.model.Model.{Book, BookItem}
import library.management.system.com.queue.MessageProducerComponent
import library.management.system.com.queue.QueueCodec.deserializer
import library.management.system.com.util.Utils.toUUID
import org.typelevel.log4cats.Logger
import io.circe.syntax.EncoderOps
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import library.management.system.com.queue.Model.BookItemMessage

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

    private def publishMessage(item: BookItem)(implicit logger: Logger[IO]): IO[Either[PublishError, ProducerResult[String, String]]] = {
      val msg = BookItemMessage.fromBookItem(item)
      implicit val accountOperationDeserializer: Deserializer[IO, Either[DeserializationError, BookItemMessage]] =
        deserializer[BookItemMessage]

      for {
        producerConfig <- config.appConfig.load[IO].map(_.queue.producer)
        result         <- messageProducer.produce(msg.isbn, msg.asJson.toString())(producerConfig)
      } yield result
    }
  }
}
