package controllers

import play.api.mvc.{ResponseHeader, SimpleResult, Action, Controller}
import play.api.libs.json.Json
import akka.actor.IOManager
import scala.io.Source
import play.api.libs.iteratee.Enumerator

/**
 * @author Valentin Kasas
 */
object Repository extends Controller {

  def getResource(uri: String) = Action {
    val file = new java.io.File(new java.net.URI(uri))
    val filename = file.getName
    val extension = filename.substring(filename.lastIndexOf('.') + 1)
    SimpleResult (
      header = ResponseHeader(200, Map(
        CONTENT_LENGTH -> file.length.toString,
        CONTENT_TYPE -> TEXT,
        "lookitt.filetype" -> extension,
        "lookitt.filename" -> filename
      )),
      body = Enumerator.fromFile(file)
    )
  }
}
