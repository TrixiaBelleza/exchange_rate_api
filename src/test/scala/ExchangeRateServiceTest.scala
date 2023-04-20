package org.formedix.exchange_rate_api
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Utils.{InvalidDateString, StringExtension, LocalDateExtension}

import org.joda.time.LocalDate

/** Tests the APIs of the ExchangeRateService and utility functions used
  * as well as error and incorrect input handling.
  */
class ExchangeRateServiceTest extends AnyFlatSpec with Matchers {
  val FileName: String = getClass.getResource("/eurofxref-hist.zip").getPath
  val ExchangeRateService = new ExchangeRateService(FileName)

  "ExchangeRateService.convertCurrency" should "correctly convert the amount from src currency to target currency" in {
    val date = "2023-03-20"
    val srcCurrency = "USD"
    val targetCurrency = "PHP"
    val amountToConvert = 100.0

    val expectedConverted = Some(5438.18232714379)
    val actualConverted = ExchangeRateService.convertCurrency(
      date,
      srcCurrency,
      targetCurrency,
      amountToConvert
    )

    actualConverted shouldEqual expectedConverted
  }

  it should "return None when targetCurrency's rate is not available in the given date" in {
    val date = "2023-04-17"
    val srcCurrency = "USD"
    val targetCurrency = "CYP"
    val amountToConvert = 100.0

    val expectedConverted = None
    val actualConverted = ExchangeRateService.convertCurrency(
      date,
      srcCurrency,
      targetCurrency,
      amountToConvert
    )

    actualConverted shouldEqual expectedConverted
  }

  it should "return None when sourceCurrency's rate is not available in the given date" in {
    val date = "2023-04-17"
    val srcCurrency = "LTL"
    val targetCurrency = "SEK"
    val amountToConvert = 100.0

    val expectedConverted = None
    val actualConverted = ExchangeRateService.convertCurrency(
      date,
      srcCurrency,
      targetCurrency,
      amountToConvert
    )

    actualConverted shouldEqual expectedConverted
  }

  it should "return None when both targetCurrency's rate and sourceCurrency's rate are not available in the given date" in {
    val date = "2023-04-17"
    val srcCurrency = "EEK"
    val targetCurrency = "SKK"
    val amountToConvert = 100.0

    val expectedConverted = None
    val actualConverted = ExchangeRateService.convertCurrency(
      date,
      srcCurrency,
      targetCurrency,
      amountToConvert
    )

    actualConverted shouldEqual expectedConverted
  }

  it should "return None when the currency does not exist in the file provided." in {
    val date = "2023-04-17"
    val srcCurrency = "non-existent"
    val targetCurrency = "SKK"
    val amountToConvert = 100.0

    val expectedConverted = None
    val actualConverted = ExchangeRateService.convertCurrency(
      date,
      srcCurrency,
      targetCurrency,
      amountToConvert
    )

    actualConverted shouldEqual expectedConverted
  }

  "ExchangeRateService.getHighestRate" should "correctly return the highest rate in a given period" in {
    val currency = "USD"
    val startDate = "2023-03-01"
    val endDate = "2023-03-31"

    val expectedHighestRate = Some(1.0886)
    val actualHighestRate =
      ExchangeRateService.getHighestRate(currency, startDate, endDate)

    actualHighestRate shouldEqual expectedHighestRate
  }

  it should "return None if there no available rates for the period" in {
    val currency = "SIT"
    val startDate = "2023-03-01"
    val endDate = "2023-03-31"

    val expectedHighestRate = None
    val actualHighestRate =
      ExchangeRateService.getHighestRate(currency, startDate, endDate)

    actualHighestRate shouldEqual expectedHighestRate
  }

  it should "return None if the given currency does not exist in the file" in {
    val currency = "XXX"
    val startDate = "2023-03-01"
    val endDate = "2023-03-31"

    val expectedHighestRate = None
    val actualHighestRate =
      ExchangeRateService.getHighestRate(currency, startDate, endDate)

    actualHighestRate shouldEqual expectedHighestRate
  }

  it should "return the highest rate for the currency with both unavailable and available values for a given period" in {
    val currency = "HRK"
    val startDate = "2022-12-23"
    val endDate = "2023-01-05"

    val expectedHighestRate = Some(7.5375)
    val actualHighestRate =
      ExchangeRateService.getHighestRate(currency, startDate, endDate)

    actualHighestRate shouldEqual expectedHighestRate
  }

  "ExchangeRateService.getAverageRate" should "correctly return the average rate in a given period" in {
    val currency = "HUF"
    val startDate = "2023-03-01"
    val endDate = "2023-03-31"

    val expectedAverageRate = Some(385.01304347826084)
    val actualAverageRate =
      ExchangeRateService.getAverageRate(currency, startDate, endDate)

    actualAverageRate shouldEqual expectedAverageRate
  }

  it should "return None if there no available rates for the period" in {
    val currency = "SKK"
    val startDate = "2019-05-01"
    val endDate = "2019-05-31"

    val expectedAverageRate = None
    val actualAverageRate =
      ExchangeRateService.getAverageRate(currency, startDate, endDate)

    actualAverageRate shouldEqual expectedAverageRate
  }

  it should "return None if the given currency does not exist in the file" in {
    val currency = "XXX"
    val startDate = "2023-03-01"
    val endDate = "2023-03-31"

    val expectedAverageRate = None
    val actualAverageRate =
      ExchangeRateService.getAverageRate(currency, startDate, endDate)

    actualAverageRate shouldEqual expectedAverageRate
  }

  it should "return the average rate for the currency with both unavailable and available values for a given period" in {
    val currency = "HRK"
    val startDate = "2022-12-23"
    val endDate = "2023-01-05"

    val expectedAverageRate = Some(7.5367999999999995)
    val actualAverageRate =
      ExchangeRateService.getAverageRate(currency, startDate, endDate)

    actualAverageRate shouldEqual expectedAverageRate
  }

  "ExchangeRateService.getRatesByDate" should "return the rates for all available currencies for a given date" in {
    val ratesForJan041999 = ExchangeRateService.getRatesByDate("1999-01-04")
    ratesForJan041999.get("BGN") shouldBe None
    ratesForJan041999.get("TRL") shouldBe Some(372274.0)
    ratesForJan041999.get("HUF") shouldBe Some(251.48)
    ratesForJan041999.get("PLN") shouldBe Some(4.0712)
    ratesForJan041999.get("ROL") shouldBe Some(13111.0)

    val ratesForApr172023 = ExchangeRateService.getRatesByDate("2023-04-17")
    ratesForApr172023.get("USD") shouldBe Some(1.0981)
    ratesForApr172023.get("NOK") shouldBe Some(11.364)
    ratesForApr172023.get("RUB") shouldBe None
    ratesForApr172023.get("CZK") shouldBe Some(23.345)
    ratesForApr172023.get("JPY") shouldBe Some(146.97)
  }

  it should "return None for all available currencies if given date is invalid" in {
    val ratesOnInvalidDate = ExchangeRateService.getRatesByDate("invalid-date")
    ratesOnInvalidDate.get("PHP") shouldBe None
    ratesOnInvalidDate.get("ZAR") shouldBe None
    ratesOnInvalidDate.get("CYP") shouldBe None
    ratesOnInvalidDate.get("RUB") shouldBe None
    ratesOnInvalidDate.get("CZK") shouldBe None
    ratesOnInvalidDate.get("JPY") shouldBe None
  }

  "ExchangeRateService.getRate" should "return the rate for a given date and currency" in {
    ExchangeRateService.getRate("2022-09-28", "USD") shouldBe Some(0.9565)
    ExchangeRateService.getRate("2021-10-11", "JPY") shouldBe Some(130.7)
    ExchangeRateService.getRate("2023-04-17", "RON") shouldBe Some(4.9425)
  }

  it should "return None for a given date and currency if the currency is N/A" in {
    ExchangeRateService.getRate("2022-08-18", "SIT") shouldBe None
    ExchangeRateService.getRate("2021-04-13", "TRL") shouldBe None
    ExchangeRateService.getRate("2023-04-06", "HRK") shouldBe None
  }

  it should "return None for a given date and currency if the currency does not exist in the file" in {
    ExchangeRateService.getRate("2022-09-28", "XXX") shouldBe None
    ExchangeRateService.getRate("2021-10-11", "YYY") shouldBe None
    ExchangeRateService.getRate("2023-04-17", "XYZ") shouldBe None
  }

  it should "return None for a given date and currency if the date does not exist in the file" in {
    ExchangeRateService.getRate("2023-03-18", "USD") shouldBe None
    ExchangeRateService.getRate("2023-03-19", "JPY") shouldBe None
    ExchangeRateService.getRate("2023-03-11", "RON") shouldBe None
  }

  it should "return None for a given date and currency if the date is invalid" in {
    ExchangeRateService.getRate("xxxx", "USD") shouldBe None
  }

  "ExchangeRateService.getExchangeRatesInRange" should "return the list of exchange rates in provided date range" in {
    val expectedRatesInRange =
      List(1.0696, 1.0723, 1.0599, 1.05, 1.0601, 1.0545, 1.0683)

    ExchangeRateService
      .getExchangeRatesInRange(
        "USD",
        "2023-01-01",
        "2023-01-10"
      ) shouldBe expectedRatesInRange
  }

  it should "return an empty list if there are no available rates for the currency in provided date range" in {
    ExchangeRateService
      .getExchangeRatesInRange(
        "TRL",
        "2023-01-01",
        "2023-01-10"
      ) shouldBe List.empty
  }

  it should "return the list of available exchange rates if there are both available and N/A rates for the currency in provided date range" in {
    val expectedRatesInRange =
      List(7.5365, 7.5365, 7.5365)

    ExchangeRateService
      .getExchangeRatesInRange(
        "HRK",
        "2022-12-28",
        "2023-01-05"
      ) shouldBe expectedRatesInRange
  }

  it should "return an empty list if dates are invalid" in {
    ExchangeRateService
      .getExchangeRatesInRange(
        "USD",
        "XXXX",
        "YYYY"
      ) shouldBe List.empty

    ExchangeRateService
      .getExchangeRatesInRange(
        "USD",
        "XXXX",
        "2023-01-01"
      ) shouldBe List.empty

    ExchangeRateService
      .getExchangeRatesInRange(
        "USD",
        "2023-01-01",
        "YYYY"
      ) shouldBe List.empty
  }

  ExchangeRateService
    .getExchangeRatesInRange(
      "USD",
      "XXXX",
      "2023-01-01"
    ) shouldBe List.empty

  "toLocalDate" should "throw InvalidDateString Exception when date string is invalid" in {
    val dateString = "invalid"
    val thrown = intercept[InvalidDateString] {
      dateString.toLocalDate
    }
    thrown.message should be(s"Invalid Date String: $dateString.")
  }

  it should "parse the date string correctly to LocalDate" in {
    "1999-01-25".toLocalDate shouldBe new LocalDate("1999-01-25")
    "2023-04-10".toLocalDate shouldBe new LocalDate("2023-04-10")
    "2021-02-27".toLocalDate shouldBe new LocalDate("2021-02-27")
  }

  "isInPeriod" should "return true if input date is equal to the startDate and before the endDate" in {
    val inputDate = new LocalDate("2021-01-01")
    val startDate = inputDate
    val endDate = new LocalDate("2022-01-01")

    inputDate.isInPeriod(startDate, endDate) shouldBe true
  }

  it should "return true if inputDate is after startDate and before the endDate" in {
    val inputDate = new LocalDate("2021-01-01")
    val startDate = new LocalDate("2020-12-31")
    val endDate = new LocalDate("2022-01-01")

    inputDate.isInPeriod(startDate, endDate) shouldBe true
  }

  it should "return false if inputDate is before the start date" in {
    val inputDate = new LocalDate("2021-11-30")
    val startDate = new LocalDate("2021-12-31")
    val endDate = new LocalDate("2022-01-01")

    inputDate.isInPeriod(startDate, endDate) shouldBe false
  }

  it should "return false if inputDate is after the endDate" in {
    val inputDate = new LocalDate("2022-01-02")
    val startDate = new LocalDate("2021-12-31")
    val endDate = new LocalDate("2022-01-01")

    inputDate.isInPeriod(startDate, endDate) shouldBe false
  }

  it should "return true if inputDate is equal to the endDate and after the startDate" in {
    val inputDate = new LocalDate("2022-01-01")
    val startDate = new LocalDate("2021-12-31")
    val endDate = inputDate

    inputDate.isInPeriod(startDate, endDate) shouldBe true
  }

  it should "return false if startDate is after endDate (erroneous inputs)" in {
    val inputDate = new LocalDate("2022-05-01")
    val startDate = new LocalDate("2022-01-01")
    val endDate = new LocalDate("2021-01-01")

    inputDate.isInPeriod(startDate, endDate) shouldBe false
  }
}
