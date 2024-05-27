package library.management.system.com.queue

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
      dueDat: String,
      isOverdue: String
  )
}
