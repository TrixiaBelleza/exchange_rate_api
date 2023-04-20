package org.formedix.exchange_rate_api

object Main {
  def main(args: Array[String]): Unit = {
    println("Foreign Exchange Rates")

    // To test in scala compiler,
    // Set Run Configuration CLI Arguments to: ./src/test/resources/eurofxref-hist.zip
    // to access the .zip file for exchange rates
    val exchangeRateService = new ExchangeRateService(args(0))

    println(s"Rates at 2023-04-06: ${exchangeRateService.getRatesByDate("2023-04-06")}")
    println(s"Highest Rate for the period of 2023-03-01 to 2023-03-31 for USD: ${exchangeRateService.getHighestRate("USD", "2023-03-01", "2023-03-31")}")
    println(s"Average Rate for the period of 2022-01-01 to 2022-12-31 for PHP: ${exchangeRateService.getAverageRate("PHP", "2022-01-01", "2022-12-31")}")
    println(s"Converted 100 USD to PHP at 2023-04-06: ${exchangeRateService.convertCurrency("2023-04-06", "USD", "PHP", 100)}")
  }
}