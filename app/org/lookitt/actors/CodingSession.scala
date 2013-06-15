package org.lookitt.actors

import akka.actor.Actor
import java.net.URL
import play.api.libs.iteratee.{Iteratee, Concurrent}

import models.User

/**
 * @author Valentin Kasas
 */
class CodingSession extends Actor{

  var users = Set[User]()
  val (enumerator, channel) = Concurrent.broadcast[String]

  def receiveInternal:PartialFunction[Any, Unit] = {


    case Connect(user) => {
      if (!users.contains(user)){
        val iteratee = Iteratee.foreach[String]{ msg =>
          self ! Emit(user, msg)
        }.mapDone { _ =>
          self ! Disconnect(user)
        }
        users += user
        println("added %s" format user)
        sender ! (iteratee, enumerator)
      } else {
        sender ! (Iteratee.ignore[String], enumerator)
      }
    }
    case Disconnect(user) => users -= user
    case Emit(user, msg) => channel.push(msg)

  }

  def receive = {
    case msg =>
    println(msg)
    receiveInternal(msg)
  }
}

case class Connect(user: User)
case class Disconnect(user: User)
case class Emit(user: User, msg: String)
