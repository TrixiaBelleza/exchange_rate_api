package org.formedix.exchange_rate_api

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Files, Paths}

/** Tests the downloading of zip file from https://www.ecb.europa.eu/
  */
class ECBExchangeRateDownloaderTest extends AnyFlatSpec with Matchers {
  behavior of "ECBExchangeRateDownloader"

  it should "download the exchange rates history zip file" in {
    val downloader = new ECBExchangeRateDownloader
    downloader.download()
    val outputFile = downloader.outputFileName
    val outputPath = Paths.get(outputFile)
    Files.exists(outputPath) shouldBe true
    Files.deleteIfExists(outputPath)
  }
}
