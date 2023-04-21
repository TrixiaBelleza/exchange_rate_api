package org.formedix.exchange_rate_api

import com.typesafe.scalalogging.StrictLogging
import Utils.InvalidSourceURLException
import org.jsoup.Jsoup

import java.io.{InputStream, OutputStream}
import java.net.{HttpURLConnection, URL}
import scala.util.{Failure, Success, Try}

trait ExchangeRateDownloader extends StrictLogging {
  val sourceURL: URL
  val outputFileName: String
  val cssQuery: String
  private val RequestMethodGet: String = "GET"

  def download(): Unit

  /** Gets the protocol://hostname of the source url
    */
  def getHostNameWithProtocol: String =
    s"${sourceURL.getProtocol}://${sourceURL.getHost}"

  /** Gets the downloadable path from the queried HTML attribute value
    * @param attribute an HTML attribute (e.g. href) that contains the downloadable path.
    * @return the downloadable path in datatype String
    */
  def getDownloadablePath(attribute: String): String = {
    Try {
      Jsoup.connect(sourceURL.toString)
    } match {
      case Success(jsoupConnection) =>
        jsoupConnection.get().select(cssQuery).attr(attribute)
      case Failure(exception) =>
        val errorMessage =
          s"Failed to connect to the sourceURL: $sourceURL due to ${exception.getMessage}"
        logger.warn(
          errorMessage,
          exception
        )
        throw InvalidSourceURLException(errorMessage)
    }
  }

  /** Gets an HttpURLConnection to the downloadable URL
    * @param downloadableURL URL where the zip can be downloaded
    * @return
    */
  def getConnection(downloadableURL: URL): Option[HttpURLConnection] = {
    Try {
      val httpURLConnection =
        downloadableURL.openConnection().asInstanceOf[HttpURLConnection]
      httpURLConnection.setRequestMethod(RequestMethodGet)

      Some(httpURLConnection)
    } match {
      case Success(connection) =>
        connection
      case Failure(exception) =>
        logger.warn(
          s"Failed to establish a connection to $downloadableURL due to ${exception.getMessage}",
          exception
        )
        None
    }
  }

  /** Downloads the zip file from the downloadable link
    * @param inputStream input stream from the downloadable url connection
    * @param outputStream output stream for the output filename
    * @param outputFileName output filename or path where the downloaded zip file will be located
    */
  def downloadFile(
      inputStream: InputStream,
      outputStream: OutputStream,
      outputFileName: String
  ): Unit = {
    try {
      val buffer = new Array[Byte](4096)
      var bytesRead = inputStream.read(buffer)
      while (bytesRead != -1) {
        outputStream.write(buffer, 0, bytesRead)
        bytesRead = inputStream.read(buffer)
      }
      logger.info(s"Downloaded zip from $sourceURL successfully!")
    } catch {
      case e: Exception =>
        logger.warn(
          s"Failed to write downloaded zip file to $outputFileName due to ${e.getMessage}",
          e
        )
    } finally {
      outputStream.close()
      inputStream.close()
    }
  }
}
