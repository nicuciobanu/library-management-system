package library.management.system.com.db.components

import cats.effect.IO
import library.management.system.com.model.Exceptions.{CreateError, _}
import library.management.system.com.model.Models.{Book, BookItem}
import library.management.system.com.util.Utils.toUUID

trait BookServiceComponent {
  this: BookRepositoryComponent with ItemRepositoryComponent =>

  val bookService: BookService

  trait BookService {
    def createBook(book: Book): IO[Either[CreateError, String]] =
      bookRepository.createBook(book)

    def getBook(isbn: String, subject: String): IO[Either[SearchError, Book]] =
      bookRepository.getBook(isbn, subject)

    def createBookItem(item: BookItem): IO[Either[CreateError, String]] =
      for {
        _ <- bookRepository.getBook(item.isbn, item.subject)
        result <- itemRepository.createItem(item)
      } yield result

    def getBookItem(barcode: String, tag: String): IO[Either[SearchError, BookItem]] = {
      val tagUUID = toUUID(tag)
      itemRepository.getItem(barcode, tagUUID)
    }

  }
}
