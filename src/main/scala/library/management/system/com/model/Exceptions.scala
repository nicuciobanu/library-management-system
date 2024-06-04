package library.management.system.com.model

import io.circe.{DecodingFailure, Json, ParsingFailure}

object Exceptions {

  trait BookManagementError

  class CreateError(message: String) extends Exception with BookManagementError {
    override def getMessage: String = message
  }
  case class BookCreateError(message: String) extends CreateError(message)
  case class ItemCreateError(message: String) extends CreateError(message)

  class SearchError(message: String) extends Exception with BookManagementError {
    override def getMessage: String = message
  }
  case class BookNotFoundError(message: String) extends SearchError(message)
  case class ItemNotFoundError(message: String) extends SearchError(message)

  case class SerializationError(message: String) extends Exception with BookManagementError {
    override def getMessage: String = message
  }

  case class LocalDateParseError(message: String) extends Exception with BookManagementError {
    override def getMessage: String = message
  }

  case class LoadConfigError(message: String) extends Exception with BookManagementError {
    override def getMessage: String = message
  }

  sealed trait DeserializationError extends BookManagementError {
    val error: Throwable
    val errorMessage: String
  }
  case class InvalidJson(rawBody: String, cause: ParsingFailure) extends Exception with DeserializationError {
    override val error: Throwable = this.getCause
    override def toString: String =
      s"Invalid-json, [$rawBody] is not a json, cause: ${cause.getMessage}"

    override val errorMessage: String = this.toString
  }

  case class InvalidEntity(jsonBody: Json, cause: DecodingFailure) extends Exception with DeserializationError {
    override val error: Throwable = this.getCause
    override def toString: String =
      s"Invalid-entity, [${jsonBody.noSpaces}], cause: ${cause.getMessage()}]"

    override val errorMessage: String = this.toString
  }

  case class UnexpectedError(throwable: Throwable) extends Exception with DeserializationError {
    override val error: Throwable = throwable
    override def toString: String = s"Unexpected error: ${throwable.getMessage}"

    override val errorMessage: String = this.toString
  }

  case class PublishError(throwable: Throwable) extends Exception with BookManagementError {
    override def toString: String = s"Kafka publisher error: ${throwable.getMessage}"
  }

  case class ConsumeError(throwable: Throwable) extends Exception with BookManagementError {
    override def toString: String = s"Kafka consumer error: ${throwable.getMessage}"
  }
}
