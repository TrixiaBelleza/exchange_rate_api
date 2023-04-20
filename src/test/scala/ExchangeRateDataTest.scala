package org.formedix.exchange_rate_api

import Utils.{ReferenceRate, StringExtension}
import org.joda.time.LocalDate
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** Tests the loading of exchange rates data from a zip file into memory.
  */
class ExchangeRateDataTest extends AnyFlatSpec with Matchers {
  val FileName: String = getClass.getResource("/eurofxref-hist.zip").getPath

  "loadData" should "be able to load data into memory in a form of a Map[LocalDate, List[ReferenceRate]]" in {
    val exchangeRateService = new ExchangeRateService(FileName)
    val exchangeRatesData = exchangeRateService.loadData()

    // Spot check the result
    val referenceRatesForDate = exchangeRatesData.get("2023-04-06".toLocalDate)

    referenceRatesForDate shouldBe defined
    referenceRatesForDate.get.find(_.currency == "USD") shouldBe Some(
      ReferenceRate("USD", Some(1.0915))
    )
    referenceRatesForDate.get.find(_.currency == "EEK") shouldBe Some(
      ReferenceRate("EEK", None)
    )
    referenceRatesForDate.get.find(_.currency == "TRY") shouldBe Some(
      ReferenceRate("TRY", Some(21.0195))
    )
    referenceRatesForDate.get.find(_.currency == "MYR") shouldBe Some(
      ReferenceRate("MYR", Some(4.8015))
    )
  }

  it should "return an empty map if the zip file is empty" in {
    val emptyZip: String = getClass.getResource("/empty-zip.zip").getPath
    val exchangeRateService = new ExchangeRateService(emptyZip)
    val exchangeRatesData = exchangeRateService.loadData()

    exchangeRatesData shouldBe Map.empty[LocalDate, List[ReferenceRate]]
  }

  it should "return an empty map if the zip does not exist" in {
    val exchangeRateService = new ExchangeRateService("non-existent.zip")
    val exchangeRatesData = exchangeRateService.loadData()

    exchangeRatesData shouldBe Map.empty[LocalDate, List[ReferenceRate]]
  }
}
