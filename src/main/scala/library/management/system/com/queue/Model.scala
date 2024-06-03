package library.management.system.com.queue

import library.management.system.com.model.Model.BookItem

object Model {
  case class BookItemMessage(
      barcode: String,
      tag: String,
      isbn: String,
      subject: String,
      title: String,
      isReferenceOnly: String,
      lang: String,
      numberOfPages: String,
      format: String,
      borrowed: String,
      loanPeriod: String,
      dueDate: String,
      isOverdue: String
  )

  object BookItemMessage {
    def fromBookItem(item: BookItem): BookItemMessage =
      BookItemMessage(
        barcode = item.barcode,
        tag = item.tag.toString,
        isbn = item.isbn,
        subject = item.subject,
        title = item.title,
        isReferenceOnly = item.isReferenceOnly.toString,
        lang = item.lang.name,
        numberOfPages = item.numberOfPages.toString,
        format = item.format.name,
        borrowed = item.borrowed.toString,
        loanPeriod = item.loanPeriod.toString,
        dueDate = item.dueDate.toString,
        isOverdue = item.isOverdue.toString
      )
  }
}
