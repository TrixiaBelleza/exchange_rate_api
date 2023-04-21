package org.formedix.exchange_rate_api

/**
  * This is to show sample usages of the ExchangeRateService API.
  */
object Main {
  def main(args: Array[String]): Unit = {
    println("Foreign Exchange Rates")

    // ********** Sample usages of the ExchangeRateService **********

    val ecbExchangeRatesLink = "https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html"
    val forexServiceViaLink = new ExchangeRateService(None)

    println(
      s"""|Exchange Rate Service where data is downloaded from this link: $ecbExchangeRatesLink
          |Rates at 2023-04-06: ${forexServiceViaLink.getRatesByDate("2023-04-06")}
          |Highest Rate for the period of 2023-03-01 to 2023-03-31 for USD: ${forexServiceViaLink.getHighestRate("USD", "2023-03-01", "2023-03-31")}"
          |Average Rate for the period of 2022-01-01 to 2022-12-31 for PHP: ${forexServiceViaLink.getAverageRate("PHP", "2022-01-01", "2022-12-31")}
          |Converted 100 USD to PHP at 2023-04-06: ${forexServiceViaLink.convertCurrency("2023-04-06", "USD", "PHP", 100)}
          |""".stripMargin)

    val pathToZip = "./src/test/resources/eurofxref-hist.zip"
    val forexServiceViaFile = new ExchangeRateService(Some(pathToZip))
    println(
      s"""|Exchange Rate Service where data is from this this path to zip: $pathToZip
          |Rates at 2023-04-06: ${forexServiceViaFile.getRatesByDate("2023-04-06")}
          |Highest Rate for the period of 2023-03-01 to 2023-03-31 for USD: ${forexServiceViaFile.getHighestRate("USD", "2023-03-01", "2023-03-31")}"
          |Average Rate for the period of 2022-01-01 to 2022-12-31 for PHP: ${forexServiceViaFile.getAverageRate("PHP", "2022-01-01", "2022-12-31")}
          |Converted 100 USD to PHP at 2023-04-06: ${forexServiceViaFile.convertCurrency("2023-04-06", "USD", "PHP", 100)}
          |""".stripMargin)

    // TODO: Before uncommenting this part, make sure to set the Run Configuration CLI Arguments first:
    // To test in scala compiler,
    // Set Run Configuration CLI Arguments to: ./src/test/resources/eurofxref-hist.zip
    // to access the .zip file for exchange rates
    // val forexServiceFromArgs = new ExchangeRateService(Option(args(0)))

    // println(
    //  s"""|Exchange Rate Service where data is from this this path to zip: $pathToZip
    //      |Rates at 2023-04-06: ${forexServiceFromArgs.getRatesByDate("2023-04-06")}
    //      |Highest Rate for the period of 2023-03-01 to 2023-03-31 for USD: ${forexServiceFromArgs.getHighestRate("USD", "2023-03-01", "2023-03-31")}"
    //      |Average Rate for the period of 2022-01-01 to 2022-12-31 for PHP: ${forexServiceFromArgs.getAverageRate("PHP", "2022-01-01", "2022-12-31")}
    //      |Converted 100 USD to PHP at 2023-04-06: ${forexServiceFromArgs.convertCurrency("2023-04-06", "USD", "PHP", 100)}
    //      |""".stripMargin)
  }
}