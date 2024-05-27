package library.management.system.com.util

import library.management.system.com.model.Exceptions.{LocalDateParseError, SerializationError}
import library.management.system.com.model.Model.{Format, Language}

import java.time.{LocalDate, Period}
import java.time.format.DateTimeFormatter
import java.util.UUID

object Utils {
  def toLanguage(language: String): Language =
    Language.valueOf(language) match {
      case Some(lang) => lang
      case _          => throw SerializationError(s"Malformed language string $language.")
    }

  def toFormat(format: String): Format =
    Format.valueOf(format) match {
      case Some(fmt) => fmt
      case _         => throw SerializationError(s"Malformed format string $format.")
    }

  def toLocalDate(pd: String): LocalDate =
    try LocalDate.parse(pd, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    catch {
      case _: Exception => throw LocalDateParseError(s"Malformed local date str $pd.")
    }

  def toUUID(uuid: String): UUID =
    try UUID.fromString(uuid)
    catch {
      case _: Exception => throw SerializationError(s"Malformed UUID string $uuid.")
    }

  def getPeriod(startDate: LocalDate, endDate: LocalDate): Period =
    Period
      .between(startDate, endDate)
}
