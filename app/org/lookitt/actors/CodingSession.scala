package org.lookitt.actors

import akka.actor.Actor
import java.net.URL
import play.api.libs.iteratee.{Iteratee, Concurrent}

/**
 * @author Valentin Kasas
 */
case class CodingSession(repository: URL, revision: String) extends Actor{

  var users = Set[User]()
  val (enumerator, channel) = Concurrent.broadcast[Diff]

  def receive = {
    case Connect(user) => {
      if (!users.contains(user)){
        val iteratee = Iteratee.foreach[Diff]{ diff =>
          self ! Emit(user, diff)
        }.mapDone { _ =>
          self ! Disconnect(user)
        }
        users += user
        sender ! (iteratee, enumerator)
      } else {
        sender ! (Iteratee.ignore[Diff], enumerator)
      }
    }
    case Disconnect(user) => users -= user
    case Emit(user, diff) => channel.push(diff)
  }
}

case class Connect(user: User)
case class Disconnect(user: User)
case class Emit(user: User, diff: Diff)

trait User
trait Diff