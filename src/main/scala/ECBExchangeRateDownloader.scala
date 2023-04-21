package org.formedix.exchange_rate_api

import Utils.FileDownloadException

import java.io.FileOutputStream
import java.net.{HttpURLConnection, URL}
import scala.util.{Failure, Success, Try}

class ECBExchangeRateDownloader extends ExchangeRateDownloader {
  private val HrefAttribute: String = "href"
  private val sourceUrlStr: String =
    "https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html"

  override val outputFileName: String = "eurofxref-hist.zip"
  override val sourceURL: URL = new URL(sourceUrlStr)
  override val cssQuery: String =
    "h4:contains(Time series) + ul > li > a.download"

  /** Downloads the eurofxref-hist.zip file from the sourceUrl
    *
    * Since the sourceUrl refers to an HTML file, the download() parses the HTML document
    * and gets the downloadable link from the href html attribute using the cssQuery.
    */
  override def download(): Unit = {
    Try {
      val downloadablePath = getDownloadablePath(HrefAttribute)
      val downloadableURL =
        new URL(s"$getHostNameWithProtocol$downloadablePath")

      for (connection <- getConnection(downloadableURL)) {
        if (connection.getResponseCode == HttpURLConnection.HTTP_OK) {
          val inputStream = connection.getInputStream
          val outputStream = new FileOutputStream(outputFileName)

          downloadFile(inputStream, outputStream, outputFileName)
        } else {
          val errorMessage =
            s"Failed to download file from $sourceURL. Response code: ${connection.getResponseCode}"
          logger.warn(errorMessage)
          throw FileDownloadException(errorMessage)
        }
      }
    } match {
      case Success(_) =>
        logger.info(s"Successfully downloaded the zip file from $sourceURL!")
      case Failure(exception) =>
        val errorMessage =
          s"Failed to download from $sourceURL due to ${exception.getMessage}"
        logger.warn(
          errorMessage,
          exception
        )
        throw FileDownloadException(errorMessage)
    }
    ()
  }
}
