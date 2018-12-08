package chatClient

import akka.actor.{Actor, ActorRef, ActorRefFactory, Props}
import spray.routing._

import scala.collection.mutable.ListBuffer

class ChatClientActor extends Actor with HttpService{
  override def receive = runRoute(aSimpleRoute)

  def actorRefFactory: ActorRefFactory = context

  var users = new ListBuffer[ActorRef]()

  val aSimpleRoute = path("join") {
    get {
      parameters("name") { (name) =>
        val actor = context.actorOf(Props[ChatClientActor], name)
        users += actor
        complete(name + " joined!")
      }
    }
  }


}
