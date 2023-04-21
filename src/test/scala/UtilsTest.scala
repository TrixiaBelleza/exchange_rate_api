package org.formedix.exchange_rate_api

import Utils.{
  InvalidDateString,
  LocalDateExtension,
  StringExtension
}
import org.joda.time.LocalDate
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UtilsTest extends AnyFlatSpec with Matchers {

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
