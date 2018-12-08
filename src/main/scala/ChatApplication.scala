import akka.actor.{ActorRef, ActorSystem, Props}
import akka.io.IO
import chatClient.ChatClientActor
import spray.can.Http


class ChatApplication {

  implicit val system = ActorSystem()

  val myListener = system.actorOf(Props[ChatClientActor], "ChatClient")

  IO(Http) ! Http.Bind(myListener, interface = "localhost", port = 8080)

}
