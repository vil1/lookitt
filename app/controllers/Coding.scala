package controllers

import org.lookitt.actors.{Connect, CodingSession}
import play.api.mvc.{AnyContent, WebSocket, Action, Controller}
import models.User
import play.api.libs.iteratee.{Enumerator, Iteratee}
import akka.actor.Props
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import play.api.libs.concurrent.Akka
import akka.pattern._

import play.api.Play.current
import java.util.UUID

/**
 * @author Valentin Kasas
 */
object Coding extends Controller {

  implicit val timeout = Timeout(1, TimeUnit.SECONDS)
  val session = Akka.system.actorOf(Props[CodingSession])


  def createSession(username:String):Action[AnyContent] = Action {
    Redirect(routes.Coding.joinSession(username, UUID.randomUUID().toString))
  }

  def joinSession(username: String, sessionId: String) = Action {
    implicit request =>
      Ok(views.html.code.joinSession(username, sessionId))
  }

  def sessionSocket(sessionId:String, username: String) = WebSocket.async {
    request =>
      val user = User.find(username)
      val future = session ? Connect(user, sessionId)
      future.mapTo[(Iteratee[String, _], Enumerator[String])]
  }

}
