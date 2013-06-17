package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import akka.actor.IOManager
import scala.io.Source

/**
 * @author Valentin Kasas
 */
object Repository extends Controller {

  def getResource(uri: String) = Action {
    val source = Source.fromURL(uri)
    val dto = Map(
      "uri" -> uri,
      "content" -> source.getLines().fold("")(_+ "\n" +_)
    )
    Ok(Json.toJson(dto))
  }


}
