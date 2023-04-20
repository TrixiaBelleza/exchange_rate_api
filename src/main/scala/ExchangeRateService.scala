package org.formedix.exchange_rate_api

import Utils.{LocalDateExtension, ReferenceRate, StringExtension}
import org.joda.time.LocalDate

import scala.util.{Failure, Success, Try}

class ExchangeRateService(val inputPath: String) extends ExchangeRateData {
  override val path: String = inputPath

  /** Retrieves the reference rate data for a given date for all
    * available currencies.
    *
    * @param date input date in string
    * @return ratesByCurrency which is a Map[String, Double] where its key is the currency and its value is the rate
    *         of that currency on a given date.
    */
  def getRatesByDate(date: String): Map[String, Double] = {
    Try {
      exchangeRatesData.get(date.toLocalDate)
    } match {
      case Success(referenceRatesOpt) =>
        val ratesByCurrency = Map.newBuilder[String, Double]

        for {
          referenceRates <- referenceRatesOpt
          referenceRate <- referenceRates
          rate <- referenceRate.rate
        } {
          ratesByCurrency += referenceRate.currency -> rate
        }

        ratesByCurrency.result()
      case Failure(exception) =>
        logger.warn(
          s"Failed to get rates for $date due to ${exception.getMessage}",
          exception
        )
        Map.empty[String, Double]
    }
  }

  /** Retrieves the reference rate data for a given date and currency
    *
    * @param date input date in string
    * @param currency input currency
    * @return None if the rate is not available for the given date, otherwise, return the rate.
    */
  def getRate(date: String, currency: String): Option[Double] = {
    getRatesByDate(date).get(currency)
  }

  /** Converts the given amountToConvert from the srcCurrency to the targetCurrency on the provided date.
    * @param date date as a basis of the exchange rate
    * @param srcCurrency source currency
    * @param targetCurrency target currency
    * @param amountToConvert amount to convert
    * @return Returns the converted currency. If the rates are not available on the given date, return None.
    */
  def convertCurrency(
      date: String,
      srcCurrency: String,
      targetCurrency: String,
      amountToConvert: Double
  ): Option[Double] = {
    for {
      srcRate <- getRate(date, srcCurrency)
      targetRate <- getRate(date, targetCurrency)
    } yield {
      amountToConvert * (targetRate / srcRate)
    }
  }

  /** Gets the highest reference exchange rate that the currency achieved from the given period.
    *
    * @param currency input currency
    * @param startDate start date of a period
    * @param endDate end date of a period
    * @return Returns None if the list of rates in a given period is empty or contains only None. Otherwise,
    *         returns the max value.
    */
  def getHighestRate(
      currency: String,
      startDate: String,
      endDate: String
  ): Option[Double] = {
    getExchangeRatesInRange(currency, startDate, endDate).maxOption
  }

  /** Gets the average reference exchange rate that the currency achieved from the given period.
    *
    * Implementation:
    * This gets the list of rates of type `List[Option[Double]]` from the exchangeRatesDataInRange
    * and then calculates for the sum. After that, it will divide the sum to the total number of elements in
    * the exchangeRatesDataInRange to get the average.
    *
    * @param currency  input currency
    * @param startDate start date of a period
    * @param endDate   end date of a period
    * @return Returns None if the list of rates in a given period is empty or contains only None. Otherwise,
    *         returns the max value.
    */
  def getAverageRate(
      currency: String,
      startDate: String,
      endDate: String
  ): Option[Double] = {
    val rates = getExchangeRatesInRange(currency, startDate, endDate)
    if (rates.nonEmpty) {
      Some(rates.sum / rates.size)
    } else {
      None
    }
  }

  private[exchange_rate_api] def getExchangeRatesInRange(
      currency: String,
      startDate: String,
      endDate: String
  ): List[Double] = {
    Try {
      val rates = for {
        (date, referenceRates) <- exchangeRatesData
        if date.isInPeriod(startDate.toLocalDate, endDate.toLocalDate)
        referenceRate <- referenceRates.find(_.currency == currency)
        rate <- referenceRate.rate
      } yield {
        rate
      }

      rates.toList
    } match {
      case Success(rates) =>
        rates
      case Failure(exception) =>
        logger.warn(
          s"Failed to get exchange rates in range due to: $exception",
          exception
        )
        List.empty[Double]
    }
  }
}
