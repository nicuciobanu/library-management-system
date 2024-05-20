package library.management.system.com.model

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
}
