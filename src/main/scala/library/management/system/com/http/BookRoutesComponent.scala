package library.management.system.com.http

import cats.effect._
import zio.json.interop.http4s.ZIOEntityCodec.zioEntityDecoder
import org.http4s._
import zio.json._
import library.management.system.com.db.components.BookServiceComponent
import library.management.system.com.http.Model.{BookRequest, ItemRequest}
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

trait BookRoutesComponent {
  this: BookServiceComponent =>

  val bookRoutes: BookRoutes

  trait BookRoutes {

    def routes(implicit logger: Logger[IO]): HttpRoutes[IO] = {
      val dsl = Http4sDsl[IO]
      import dsl._

      HttpRoutes.of[IO] {
        // books
        case request @ POST -> Root / "books" =>
          for {
            reqBody <- request.as[BookRequest]
            book = BookRequest.toBook.apply(reqBody)
            createResponse <- bookService.createBook(book)
            response <- createResponse.fold(
              error => {
                logger.error(
                  s"BookRoutesComponent :: while creating the book with isbn: ${book.isbn} and subject: ${book.subject} an error were thrown: $error!!!"
                )
                BadRequest(error.getMessage)
              },
              isbn => Created(isbn.toJson)
            )
          } yield response

        case _ @GET -> Root / "books" / isbn / subject =>
          for {
            storedBook <- bookService.getBook(isbn, subject)
            response <- storedBook.fold(
              error => {
                logger.error(
                  s"BookRoutesComponent :: while getting the book with isbn: $isbn and subject: $subject an error were thrown: $error!!!"
                )
                NotFound()
              },
              book => Ok(book.toJson)
            )
          } yield response

        // book items
        case request @ POST -> Root / "items" =>
          for {
            reqBody <- request.as[ItemRequest]
            item = ItemRequest.toItem.apply(reqBody)
            createResponse <- bookService.createBookItem(item)
            response <- createResponse.fold(
              error => {
                logger.error(
                  s"BookRoutesComponent :: while creating the item with barcode: ${item.barcode} and tag: ${item.tag} an error were thrown: $error!!!"
                )
                BadRequest(error.getMessage)
              },
              barcode => Created(barcode.toJson)
            )
          } yield response

        case _ @GET -> Root / "items" / barcode / tag =>
          for {
            storedItem <- bookService.getBookItem(barcode, tag)
            response <- storedItem.fold(
              error => {
                logger.error(
                  s"BookRoutesComponent :: while getting the item with barcode: $barcode and tag: $tag an error were thrown: $error!!!"
                )
                NotFound()
              },
              item => Ok(item.toJson)
            )
          } yield response
      }
    }
  }
}
