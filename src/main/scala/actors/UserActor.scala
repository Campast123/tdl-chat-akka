package actors

import java.io.{BufferedReader, PrintStream}
import java.net.Socket

import actors.ChatActor._
import akka.actor.Actor

class UserActor(in: BufferedReader, out: PrintStream, sock: Socket) extends Actor{

  def receive = {
    case ChatMessage(message) =>
      out.println(message)

    case doChat() =>
      this.doChat()
  }

  def doChat(): Unit ={
    nonBlockingRead(this.in).foreach{ input =>
      if(input == ":quit"){
        this.logout()
      } else {
        out.println("asddddddddddddddd")
        out.println(input)
      }
    }
  }

  def nonBlockingRead(in: BufferedReader) : Option[String] = {
    if(in.ready()) Some(in.readLine()) else None
  }

  def logout() = {
    this.sock.close()
  }


}
