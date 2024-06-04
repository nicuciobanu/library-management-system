package library.management.system.com.model

import cats.effect.IO
import library.management.system.com.model.Exceptions.SerializationError
import org.http4s.EntityEncoder
import zio.json.interop.http4s.jsonEncoderOf

import java.time.LocalDate
import java.util.UUID
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

object Model {

  sealed trait Language extends Product with Serializable {
    def name: String
  }

  case object English extends Language {
    override def name: String = "English"
  }
  case object French extends Language {
    override def name: String = "French"
  }
  case object German extends Language {
    override def name: String = "German"
  }
  case object Spanish extends Language {
    override def name: String = "Spanish"
  }
  case object Italian extends Language {
    override def name: String = "Italian"
  }

  object Language {
    def valueOf(language: String): Language = language match {
      case value if value.equals("English") => English
      case value if value.equals("French")  => French
      case value if value.equals("German")  => German
      case value if value.equals("Spanish") => Spanish
      case value if value.equals("Italian") => Italian
      case _                                => throw SerializationError(s"An error while parsing language value: $language.")
    }

    implicit val decoder: JsonDecoder[Language] = DeriveJsonDecoder.gen[Language]
    implicit val encoder: JsonEncoder[Language] = DeriveJsonEncoder.gen[Language]
  }

  sealed trait Format {
    def name: String
  }
  case object Paperback extends Format {
    override def name: String = "Paperback"
  }
  case object Hardcover extends Format {
    override def name: String = "Hardcover"
  }
  case object Audiobook extends Format {
    override def name: String = "Audiobook"
  }
  case object AudioCd extends Format {
    override def name: String = "AudioCd"
  }
  case object Mp3Cd extends Format {
    override def name: String = "Mp3Cd"
  }
  case object Pdf extends Format {
    override def name: String = "Pdf"
  }

  object Format {
    def valueOf(format: String): Format = format match {
      case value if value.equals("Paperback") => Paperback
      case value if value.equals("Hardcover") => Hardcover
      case value if value.equals("Audiobook") => Audiobook
      case value if value.equals("AudioCd")   => AudioCd
      case value if value.equals("Mp3Cd")     => Mp3Cd
      case value if value.equals("Pdf")       => Pdf
      case _                                  => throw SerializationError(s"An error while parsing format value: $format.")
    }

    implicit val decoder: JsonDecoder[Format] = DeriveJsonDecoder.gen[Format]
    implicit val encoder: JsonEncoder[Format] = DeriveJsonEncoder.gen[Format]
  }

  sealed trait AccountStatus {
    def name: String
  }
  case object Active extends AccountStatus {
    override def name: String = "Active"
  }
  case object Frozen extends AccountStatus {
    override def name: String = "Frozen"
  }
  case object Closed extends AccountStatus {
    override def name: String = "Closed"
  }

  object AccountStatus {
    def valueOf(name: String): Option[AccountStatus] = name match {
      case value if value.equals("Active") => Some(Active)
      case value if value.equals("Frozen") => Some(Frozen)
      case value if value.equals("Closed") => Some(Closed)
      case _                               => None
    }

    implicit val decoder: JsonDecoder[AccountStatus] = DeriveJsonDecoder.gen[AccountStatus]
    implicit val encoder: JsonEncoder[AccountStatus] = DeriveJsonEncoder.gen[AccountStatus]
  }

  case class Book(
      isbn: String,
      name: String,
      subject: String,
      overview: String,
      publisher: String,
      publicationDate: LocalDate,
      lang: Language
  )

  object Book {
    implicit val decoder: JsonDecoder[Book]             = DeriveJsonDecoder.gen[Book]
    implicit val encoder: JsonEncoder[Book]             = DeriveJsonEncoder.gen[Book]
    implicit def entityEncoder: EntityEncoder[IO, Book] = jsonEncoderOf[IO, Book]
  }

  case class BookItem(
      barcode: String,
      tag: UUID,
      isbn: String,
      subject: String,
      title: String,
      isReferenceOnly: Boolean,
      lang: Language,
      numberOfPages: Int,
      format: Format,
      borrowed: LocalDate,
      loanPeriod: Int,
      dueDate: LocalDate,
      isOverdue: Boolean
  )

  object BookItem {
    implicit val decoder: JsonDecoder[BookItem]             = DeriveJsonDecoder.gen[BookItem]
    implicit val encoder: JsonEncoder[BookItem]             = DeriveJsonEncoder.gen[BookItem]
    implicit def entityEncoder: EntityEncoder[IO, BookItem] = jsonEncoderOf[IO, BookItem]
  }

  case class Author(name: String, biography: String, birth: LocalDate)

  case class Account(id: Int, history: List[String], opened: LocalDate, status: AccountStatus)

  case class Address(city: String, street: String, postalCode: String)

  case class Library(name: String, address: Address)

  case class FullName(firstName: String, lastName: String)

  case class Patron(name: FullName, address: Address)

  case class Librarian(name: FullName, address: Address, position: String)

}
