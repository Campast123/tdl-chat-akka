package actors

import java.io.{BufferedReader}

import actors.ChatActor._
import akka.actor.Actor

object UserActor {

  sealed trait Event
  final case class ListenNewInput() extends Event
  final case class PrintMessage(message: String) extends Event

  class UserActor(user: User) extends Actor {

    def receive = {
      case PrintMessage(message) =>
        this.user.out.println(message)

      case ListenNewInput() =>
        this.listenNewInput()
    }

    def listenNewInput(): Unit = {
      nonBlockingRead(this.user.in).foreach { input =>
        if (input == ":quit") {
          context.parent ! Logout(this.user)
        } else {
          context.parent ! SendMessageChat(this.user, input)
        }
      }
    }

    def nonBlockingRead(in: BufferedReader): Option[String] = {
      if (in.ready()) Some(in.readLine()) else None
    }

  }

}