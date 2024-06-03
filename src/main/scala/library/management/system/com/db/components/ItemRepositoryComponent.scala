package library.management.system.com.db.components

import cats.effect.{IO, Resource}
import doobie.Transactor
import doobie.implicits._
import doobie.postgres.implicits._
import MetaMappings._
import library.management.system.com.model.Exceptions.{CreateError, ItemCreateError, ItemNotFoundError, SearchError}
import library.management.system.com.model.Model.BookItem
import org.typelevel.log4cats.Logger

import java.util.UUID

trait ItemRepositoryComponent {

  val itemRepository: ItemRepository

  class ItemRepository(resource: Resource[IO, Transactor[IO]]) {
    def createItem(item: BookItem)(implicit logger: Logger[IO]): IO[Either[CreateError, String]] = {
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
           """.stripMargin.update
          .withUniqueGeneratedKeys[String]("barcode")

      resource.use { tr =>
        createQuery
          .transact(tr)
          .attempt
          .flatMap {
            case Right(barcode) =>
              logger.info(s"Book item created with success ${item.tag}!").toResource
              IO.pure(Right(barcode))
            case Left(error) =>
              logger
                .error(
                  s"ItemRepositoryComponent :: while creating the book with barcode: ${item.barcode} and tag: ${item.tag} an error were thrown: $error!!!"
                )
                .toResource
              IO.pure(
                Left(
                  ItemCreateError(
                    s"Error while creating book item with barcode ${item.barcode} and tag ${item.tag}"
                  )
                )
              )
          }
      }
    }

    def getItem(barcode: String, tag: UUID)(implicit logger: Logger[IO]): IO[Either[SearchError, BookItem]] = {
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
           """.stripMargin
          .query[BookItem]
          .option

      resource.use { tr: Transactor[IO] =>
        getQuery
          .transact(tr)
          .attempt
          .flatMap {
            case Right(Some(value)) =>
              logger.info(s"Get item by barcode: $barcode and tag: $tag.")
              IO.pure(Right(value))
            case Right(None) =>
              logger.warn(
                s"Bo book item found with barcode: $barcode and subject: $tag!!!"
              )
              IO.pure(Left(ItemNotFoundError(s"Book item with barcode $barcode and tag $tag not found.")))
            case Left(error) =>
              logger.error(
                s"While getting the book item with barcode: $barcode and subject: $tag an error were thrown: $error!!!"
              )
              IO.pure(Left(ItemNotFoundError(s"Book item with barcode $barcode and tag $tag not found.")))
          }
      }
    }
  }
}
