package org.lookitt.actors

import akka.actor.{ActorRef, Actor}
import java.net.URL
import play.api.libs.iteratee.{Enumerator, Iteratee, Concurrent}

import Concurrent.Channel
import models.User
import java.util.UUID

/**
 * @author Valentin Kasas
 */
class CodingSession extends Actor{

  import CodingSession._


  var activeSessions = Map[String, Session]()

  def receive=  {


    case Connect(user, sessionId) => {
      val session = activeSessions.getOrElse(sessionId, {
        val (enum, chan) = Concurrent.broadcast[Message]
        Session(sessionId, enum, chan, user)
      })
      activeSessions += (sessionId -> session)
      println(session, user)
      if (!session.users.contains(user)){
        session.connect(user, self, sender)
      } else {
        sender ! (Iteratee.ignore[Message], session.enumerator)
      }

    }
    case Disconnect(user, session) => {
      session.users -= user
      if (session.users.isEmpty){
        activeSessions -= session.id
      }
    }
    case Emit(user, msg, session) => session.channel.push(msg)

  }

}

object CodingSession{

  type Message = String


  case class Session(id: String, enumerator: Enumerator[Message], channel: Channel[Message], owner:User){
    var users = Set[User]()

    def connect(user: User, actor: ActorRef, sender: ActorRef){
      users += user
      val iteratee = Iteratee.foreach[Message]{ msg =>
        actor ! Emit(user, msg, this)
      }.mapDone { _ =>
        actor ! Disconnect(user, this)
      }
      users += user
      println("added %s [%s]" format (user, users))
      sender ! (iteratee, enumerator)

    }
  }

}

case class Connect(user: User, sessionId : String )
case class Disconnect(user: User, session: CodingSession.Session)
case class Emit(user: User, msg: String, session: CodingSession.Session)