package org.formedix.exchange_rate_api

import com.typesafe.scalalogging.StrictLogging
import Utils.{NotApplicable, ReferenceRate, StringExtension}

import org.joda.time.LocalDate

import java.io.{FileInputStream, FileNotFoundException}
import java.util.zip.ZipInputStream
import scala.io.BufferedSource
import scala.util.{Failure, Success, Try}

trait ExchangeRateData extends StrictLogging {
  val path: String
  lazy val exchangeRatesData: Map[LocalDate, List[ReferenceRate]] = loadData()

  /** Loads the data from a csv file
    *
    * @return a Map of Date to its corresponding ReferenceRate(currency, rate)
    */
  def loadData(): Map[LocalDate, List[ReferenceRate]] = {
    Try {
      val fileInputStream = new FileInputStream(path)
      val zipInputStream = new ZipInputStream(fileInputStream)

      try {
        Option(zipInputStream.getNextEntry) match {
          case Some(_) =>
            transformDataFromCsv(zipInputStream)
          case None =>
            logger.warn(s"Zip: $path does not contain any file.")
            Map.empty[LocalDate, List[ReferenceRate]]
        }
      } catch {
        case exception: Exception =>
          logger.warn(
            s"Failed to load data for path: $path due to ${exception.getMessage}",
            exception
          )
          Map.empty[LocalDate, List[ReferenceRate]]
      } finally {
        fileInputStream.close()
        zipInputStream.close()
      }
    } match {
      case Success(data) =>
        data
      case Failure(exception: FileNotFoundException) =>
        logger.warn(s"File not found at path: $path.", exception)
        Map.empty[LocalDate, List[ReferenceRate]]
      case Failure(exception) =>
        logger.warn(
          s"Failed to load data for path: $path due to ${exception.getMessage}",
          exception
        )
        Map.empty[LocalDate, List[ReferenceRate]]
    }
  }

  private def transformDataFromCsv(
      zipInputStream: ZipInputStream
  ): Map[LocalDate, List[ReferenceRate]] = {
    val bufferedSource = new BufferedSource(zipInputStream)
    try {
      val currencyCodes = getCurrencyCodes(bufferedSource)

      val dataBuilder = Map.newBuilder[LocalDate, List[ReferenceRate]]
      for (row <- bufferedSource.getLines().map(_.split(",").toList)) {
        dataBuilder += row.head.toLocalDate -> getReferenceRatesList(
          row,
          currencyCodes
        )
      }

      dataBuilder.result()
    } catch {
      case exception: Exception =>
        logger.warn(
          s"Failed to transform data from csv for path: $path due to ${exception.getMessage}",
          exception
        )
        Map.empty[LocalDate, List[ReferenceRate]]
    } finally {
      bufferedSource.close()
    }
  }

  private def getCurrencyCodes(bufferedSource: BufferedSource): List[String] = {
    bufferedSource.getLines().nextOption() match {
      case Some(firstLine) =>
        firstLine
          .split(",")
          .tail // exclude the first column because it's a "date" column
          .toList
      case None =>
        logger.warn(s"The file $path does not contain any data.")
        List.empty
    }
  }

  private def getReferenceRatesList(
      row: List[String],
      currencyCodes: List[String]
  ): List[ReferenceRate] = {
    val referenceRatesBuilder = List.newBuilder[ReferenceRate]

    for {
      (rate, columnNumber) <- row.tail.zipWithIndex
    } {
      val currency = currencyCodes(columnNumber)
      val rateOption = if (rate != NotApplicable) {
        rate.toDoubleOption
      } else {
        None
      }
      referenceRatesBuilder += ReferenceRate(currency, rateOption)
    }

    referenceRatesBuilder.result()
  }
}
