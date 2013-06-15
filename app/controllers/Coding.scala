package controllers

import play.api.mvc.{WebSocket, Action, Controller}
import org.lookitt.actors.{Connect, CodingSession}
import models.User
import play.api.libs.iteratee.{Enumerator, Iteratee}
import akka.actor.Props
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import play.api.libs.concurrent.Akka
import akka.pattern._

import play.api.Play.current
/**
 * @author Valentin Kasas
 */
object Coding extends Controller {

  implicit val timeout = Timeout(1, TimeUnit.SECONDS)
  val session = Akka.system.actorOf(Props[CodingSession])

  def joinSession(username: String) = Action {
    implicit request =>
      Ok(views.html.code.joinSession(username))
  }

  def sessionSocket(username: String) = WebSocket.async {
    request =>
      val user = User.find(username)
      val future = session ? Connect(user)
      future.mapTo[(Iteratee[String, _], Enumerator[String])]
  }

}
