package library.management.system.com.queue

import library.management.system.com.model.Model.{BookItem, Format, Language}

import java.time.LocalDate
import java.util.UUID

object Model {
  case class BookItemMessage(
      barcode: String,
      tag: UUID,
      isbn: String,
      subject: String,
      title: String,
      isReferenceOnly: Boolean,
      lang: String,
      numberOfPages: Int,
      format: String,
      borrowed: LocalDate,
      loanPeriod: Int,
      dueDate: LocalDate,
      isOverdue: Boolean
  )

  object BookItemMessage {
    def fromBookItem(item: BookItem): BookItemMessage =
      BookItemMessage(
        barcode = item.barcode,
        tag = item.tag,
        isbn = item.isbn,
        subject = item.subject,
        title = item.title,
        isReferenceOnly = item.isReferenceOnly,
        lang = item.lang.name,
        numberOfPages = item.numberOfPages,
        format = item.format.name,
        borrowed = item.borrowed,
        loanPeriod = item.loanPeriod,
        dueDate = item.dueDate,
        isOverdue = item.isOverdue
      )

    def toBookItem(item: BookItemMessage): BookItem =
      BookItem(
        barcode = item.barcode,
        tag = item.tag,
        isbn = item.isbn,
        subject = item.subject,
        title = item.title,
        isReferenceOnly = item.isReferenceOnly,
        lang = Language.valueOf(item.lang),
        numberOfPages = item.numberOfPages,
        format = Format.valueOf(item.format),
        borrowed = item.borrowed,
        loanPeriod = item.loanPeriod,
        dueDate = item.dueDate,
        isOverdue = item.isOverdue
      )
  }
}
