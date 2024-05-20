package library.management.system.com.http

import cats.effect._
import zio.json.interop.http4s.ZIOEntityCodec.zioEntityDecoder
import org.http4s._
import zio.json._
import library.management.system.com.db.components.BookServiceComponent
import library.management.system.com.http.Models.{BookRequest, ItemRequest}
import org.http4s.dsl.Http4sDsl

trait BookRoutesComponent {
  this: BookServiceComponent =>

  val bookRoutes: BookRoutes

  trait BookRoutes {

    def routes: HttpRoutes[IO] = {
      val dsl = Http4sDsl[IO]
      import dsl._

      HttpRoutes.of[IO] {
        // books
        case request @ POST -> Root / "books" =>
          for {
            reqBody <- request.as[BookRequest]
            book = BookRequest.toBook.apply(reqBody)
            createResponse <- bookService.createBook(book)
            response <- createResponse.fold(err => BadRequest(err.getMessage), isbn => Created(isbn.toJson))
          } yield response

        case _ @GET -> Root / "books" / isbn / subject =>
          for {
            storedBook <- bookService.getBook(isbn, subject)
            response <- storedBook.fold(_ => NotFound(), book => Ok(book.toJson))
          } yield response

        // book items
        case request @ POST -> Root / "items" =>
          for {
            reqBody <- request.as[ItemRequest]
            item = ItemRequest.toItem.apply(reqBody)
            createResponse <- bookService.createBookItem(item)
            response <- createResponse.fold(
              err => BadRequest(err.getMessage),
              barcode => Created(barcode.toJson),
            )
          } yield response

        case _ @GET -> Root / "items" / barcode / tag =>
          for {
            storedItem <- bookService.getBookItem(barcode, tag)
            response <- storedItem.fold(_ => NotFound(), item => Ok(item.toJson))
          } yield response
      }
    }
  }
}
