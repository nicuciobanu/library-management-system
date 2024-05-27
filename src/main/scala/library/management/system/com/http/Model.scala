package library.management.system.com.http

import cats.effect.IO
import library.management.system.com.model.Model.{Book, BookItem}
import library.management.system.com.util.Utils._
import org.http4s.EntityEncoder
import zio.json.interop.http4s.jsonEncoderOf
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.time.LocalDate

object Model {

  case class BookRequest(
      isbn: String,
      name: String,
      subject: String,
      overview: String,
      publisher: String,
      publicationDate: String,
      lang: String
  )

  object BookRequest {
    def toBook: BookRequest => Book = { req =>
      Book(
        isbn = req.isbn,
        name = req.name,
        subject = req.subject,
        overview = req.overview,
        publisher = req.publisher,
        publicationDate = toLocalDate(req.publicationDate),
        lang = toLanguage(req.lang)
      )
    }

    implicit val decoder: JsonDecoder[BookRequest]             = DeriveJsonDecoder.gen[BookRequest]
    implicit val encoder: JsonEncoder[BookRequest]             = DeriveJsonEncoder.gen[BookRequest]
    implicit def entityEncoder: EntityEncoder[IO, BookRequest] = jsonEncoderOf[IO, BookRequest]
  }

  case class ItemRequest(
      barcode: String,
      tag: String,
      isbn: String,
      subject: String,
      title: String,
      isReferenceOnly: Boolean,
      lang: String,
      numberOfPages: Int,
      format: String,
      borrowed: String,
      dueDate: String
  )

  object ItemRequest {

    def toItem: ItemRequest => BookItem = { req =>
      val borrowed   = toLocalDate(req.borrowed)
      val dueDate    = toLocalDate(req.dueDate)
      val loanPeriod = getPeriod(borrowed, dueDate).getDays
      val isOverdue  = LocalDate.now().isAfter(dueDate)

      BookItem(
        barcode = req.barcode,
        tag = toUUID(req.tag),
        isbn = req.isbn,
        subject = req.subject,
        title = req.title,
        isReferenceOnly = req.isReferenceOnly,
        lang = toLanguage(req.lang),
        numberOfPages = req.numberOfPages,
        format = toFormat(req.format),
        borrowed = borrowed,
        loanPeriod = loanPeriod,
        dueDate = dueDate,
        isOverdue = isOverdue
      )
    }

    implicit val decoder: JsonDecoder[ItemRequest]             = DeriveJsonDecoder.gen[ItemRequest]
    implicit val encoder: JsonEncoder[ItemRequest]             = DeriveJsonEncoder.gen[ItemRequest]
    implicit def entityEncoder: EntityEncoder[IO, ItemRequest] = jsonEncoderOf[IO, ItemRequest]
  }
}
