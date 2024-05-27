package library.management.system.com.db.components

import cats.effect.IO
import library.management.system.com.model.Exceptions._
import library.management.system.com.model.Model.{Book, BookItem}
import library.management.system.com.util.Utils.toUUID
import org.typelevel.log4cats.Logger

trait BookServiceComponent {
  this: BookRepositoryComponent with ItemRepositoryComponent =>

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
      } yield result

    def getBookItem(barcode: String, tag: String)(implicit logger: Logger[IO]): IO[Either[SearchError, BookItem]] = {
      val tagUUID = toUUID(tag)
      itemRepository.getItem(barcode, tagUUID)
    }
  }
}
