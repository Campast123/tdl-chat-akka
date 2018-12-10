package actors

import java.io.{BufferedReader, PrintStream}
import java.net.Socket

import actors.ChatActor._
import akka.actor.Actor

class UserActor(user: User) extends Actor{

  def receive = {
    case ChatMessage(message) =>
      this.user.out.println(message)

    case doChat() =>
      this.doChat()
  }

  def doChat(): Unit ={
    nonBlockingRead(this.user.in).foreach{ input =>
      if(input == ":quit"){
        context.parent ! Logout(this.user)
      } else {
        context.parent ! SendMessageChat(user, input)
      }
    }
  }

  def nonBlockingRead(in: BufferedReader) : Option[String] = {
    if(in.ready()) Some(in.readLine()) else None
  }


}
