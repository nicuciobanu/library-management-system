package library.management.system.com.db.components

import cats.effect.{IO, Resource}
import doobie.Transactor
import doobie.implicits._
import doobie.postgres.implicits._
import MetaMappings._
import library.management.system.com.model.Exceptions._
import library.management.system.com.model.Models.Book

trait BookRepositoryComponent {

  val bookRepository: BookRepository
  class BookRepository(resource: Resource[IO, Transactor[IO]]) {

    def createBook(book: Book): IO[Either[BookCreateError, String]] = {
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
           """
          .stripMargin
          .update
          .withUniqueGeneratedKeys[String]("isbn")

      resource.use { tr =>
        createQuery
          .transact(tr)
          .attempt
          .flatMap {
            case Left(value) =>
              IO.pure(Left(BookCreateError(s"Errors while creating book ${book.isbn} ${value.getMessage}")))
            case Right(value) => IO.pure(Right(value))
          }
      }
    }

    def getBook(isbn: String, subject: String): IO[Either[SearchError, Book]] = {
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
           """
          .stripMargin
          .query[Book]
          .option

      resource.use { tr: Transactor[IO] =>
        getQuery
          .transact(tr)
          .attempt
          .flatMap {
            case Right(Some(value)) => IO.pure(Right(value))
            case _ =>
              IO.pure(Left(BookNotFoundError(s"Book with isbn $isbn and subject $subject not found.")))
          }
      }
    }
  }
}