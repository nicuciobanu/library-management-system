package library.management.system.com.db.components

import cats.effect.{IO, Resource}
import doobie.Transactor
import doobie.implicits._
import doobie.postgres.implicits._
import MetaMappings._
import library.management.system.com.model.Exceptions._
import library.management.system.com.model.Model.Book
import org.typelevel.log4cats.Logger

trait BookRepositoryComponent {

  val bookRepository: BookRepository
  class BookRepository(resource: Resource[IO, Transactor[IO]]) {

    def createBook(book: Book)(implicit logger: Logger[IO]): IO[Either[BookCreateError, String]] = {
      val createQuery =
        sql"""
          INSERT INTO book(
            isbn,
            name,
            subject,
            overview,
            publisher,
            publication_date,
            lang
          ) VALUES(
          ${book.isbn},
          ${book.name},
          ${book.subject},
          ${book.overview},
          ${book.publisher},
          ${book.publicationDate},
          ${book.lang}
          )
           """.stripMargin.update
          .withUniqueGeneratedKeys[String]("isbn")

      resource.use { tr =>
        createQuery
          .transact(tr)
          .attempt
          .flatMap {
            case Left(error) =>
              logger.error(
                s"While creating the book with isbn: ${book.isbn} and subject: ${book.subject} an error were thrown: $error!!!"
              )
              IO.pure(Left(BookCreateError(s"Errors while creating book ${book.isbn} ${error.getMessage}")))
            case Right(value) =>
              logger.info(s"Book created with success with isbn: ${book.isbn} and subject: ${book.subject}.")
              IO.pure(Right(value))
          }
      }
    }

    def getBook(isbn: String, subject: String)(implicit logger: Logger[IO]): IO[Either[SearchError, Book]] = {
      val getQuery =
        sql"""
             SELECT
             isbn,
             name,
             subject,
             overview,
             publisher,
             publication_date,
             lang
             FROM book
             WHERE isbn = $isbn AND subject = $subject
           """.stripMargin
          .query[Book]
          .option

      resource.use { tr: Transactor[IO] =>
        getQuery
          .transact(tr)
          .attempt
          .flatMap {
            case Right(Some(value)) =>
              logger.info(s"Book found by isbn: $isbn and $subject.").toResource
              IO.pure(Right(value))
            case Right(None) =>
              logger
                .warn(
                  s"No book found with isbn: $isbn and subject: $subject!!!"
                )
                .toResource
              IO.pure(Left(BookNotFoundError(s"Book with isbn $isbn and subject $subject not found.")))
            case Left(error) =>
              logger
                .error(
                  s"While getting the book with isbn: $isbn and subject: $subject an error were thrown: $error!!!"
                )
                .toResource
              IO.pure(Left(BookNotFoundError(s"Book with isbn $isbn and subject $subject not found.")))
          }
      }
    }
  }
}
