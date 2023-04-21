package org.formedix.exchange_rate_api


import com.typesafe.scalalogging.StrictLogging
import org.joda.time.LocalDate

import scala.util.{Failure, Success, Try}

object Utils extends StrictLogging {
  val NotApplicable = "N/A"

  final case class ReferenceRate(currency: String, rate: Option[Double])

  final case class InvalidDateString(message: String, throwable: Throwable) extends Exception
  final case class InvalidSourceURLException(message: String) extends Exception
  final case class FileDownloadException(message: String) extends Exception

  implicit class StringExtension(val string: String) extends AnyVal {
    def toLocalDate: LocalDate = {
      Try {
        LocalDate.parse(string)
      } match {
        case Success(parsed) =>
          parsed
        case Failure(exception) =>
          logger.warn(s"Failed to parse string: $string to date due to ${exception.getMessage}", exception)
          throw InvalidDateString(s"Invalid Date String: $string.", exception)
      }
    }
  }

  implicit class LocalDateExtension(val localDate: LocalDate) extends AnyVal {
    def isInPeriod(startDate: LocalDate, endDate: LocalDate): Boolean = {
      // Inclusive of start date and end date when checking if localDate is within the given range.
      !localDate.isBefore(startDate) && !localDate.isAfter(endDate)
    }
  }
}
