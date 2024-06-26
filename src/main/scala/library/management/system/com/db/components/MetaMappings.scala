package library.management.system.com.db.components

import doobie.Meta
import library.management.system.com.model.Exceptions.SerializationError
import library.management.system.com.model.Model._

import scala.util.Try

object MetaMappings {

  implicit val languageMeta: Meta[Language] =
    Meta[String]
      .timap(name => Try(Language.valueOf(name)).getOrElse(throw SerializationError(s"Value $name is not of Language type.")))(_.name)

  implicit val formatMeta: Meta[Format] =
    Meta[String].timap(name => Try(Format.valueOf(name)).getOrElse(throw SerializationError(s"Value $name is not of Format type.")))(_.name)

  implicit val accountStatusMeta: Meta[AccountStatus] =
    Meta[String].timap(name =>
      AccountStatus
        .valueOf(name)
        .getOrElse(throw SerializationError(s"Value $name is not of AccountStatus type."))
    )(_.name)
}
