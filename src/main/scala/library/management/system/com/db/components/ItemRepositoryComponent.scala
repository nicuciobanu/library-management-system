package library.management.system.com.db.components

import cats.effect.{IO, Resource}
import doobie.Transactor
import doobie.implicits._
import doobie.postgres.implicits._
import MetaMappings._
import library.management.system.com.model.Exceptions.{CreateError, ItemCreateError, ItemNotFoundError, SearchError}
import library.management.system.com.model.Models.BookItem

import java.util.UUID

trait ItemRepositoryComponent {

  val itemRepository: ItemRepository

  class ItemRepository(resource: Resource[IO, Transactor[IO]]) {
    def createItem(item: BookItem): IO[Either[CreateError, String]] = {
      val createQuery =
        sql"""
              INSERT INTO book_item(
                barcode,
                tag,
                isbn,
                subject,
                title,
                is_reference_only,
                lang,
                number_of_pages,
                format,
                borrowed,
                loan_period,
                due_date,
                is_overdue
              ) VALUES(
                ${item.barcode},
                ${item.tag},
                ${item.isbn},
                ${item.subject},
                ${item.title},
                ${item.isReferenceOnly},
                ${item.lang},
                ${item.numberOfPages},
                ${item.format},
                ${item.borrowed},
                ${item.loanPeriod},
                ${item.dueDate},
                ${item.isOverdue}
              )
           """
          .stripMargin
          .update
          .withUniqueGeneratedKeys[String]("barcode")

      resource.use { tr =>
        createQuery
          .transact(tr)
          .attempt
          .flatMap {
            case Right(barcode) => IO.pure(Right(barcode))
            case _ =>
              IO.pure(
                Left(
                  ItemCreateError(
                    s"Error while creating book item with barcode ${item.barcode} and tag ${item.tag}",
                  ),
                ),
              )
          }
      }
    }

    def getItem(barcode: String, tag: UUID): IO[Either[SearchError, BookItem]] = {
      val getQuery =
        sql"""
             SELECT
                barcode,
                tag,
                isbn,
                subject,
                title,
                is_reference_only,
                lang,
                number_of_pages,
                format,
                borrowed,
                loan_period,
                due_date,
                is_overdue
             FROM book_item
             WHERE barcode = $barcode AND tag = $tag
           """
          .stripMargin
          .query[BookItem]
          .option

      resource.use { tr: Transactor[IO] =>
        getQuery
          .transact(tr)
          .attempt
          .flatMap {
            case Right(Some(value)) => IO.pure(Right(value))
            case _ =>
              IO.pure(Left(ItemNotFoundError(s"Book item with barcode $barcode and tag $tag not found.")))
          }
      }
    }
  }
}
