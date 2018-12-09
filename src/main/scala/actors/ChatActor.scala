package actors

import java.io.BufferedReader
import java.util.concurrent.ConcurrentHashMap

import actors.ActorChatServer.chatActor
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._
import scala.concurrent.Future

object ChatActor {
  sealed trait Event
  final case class Join(user: User, actorSystem: ActorSystem) extends Event
  final case class Logout(user: User) extends Event
  final case class SendMessageChat(user: User, message: String) extends Event
  final case class ChatMessage(message: String) extends Event
  final case class CheckUsersActivity() extends Event
  final case class doChat() extends Event

  class ChatActor extends Actor{
    val users = new ConcurrentHashMap[String,ActorRef]().asScala

    def receive = {
      case Join(user, actorSystem) =>
        users += user.name -> this.createUserActor(user, actorSystem)
        this.messageAllUsers(user,"Estoy en el chat")
      case Logout(user) =>
        this.messageAllUsers(user,"Chau chau chauuuuu")
        users -= user.name
        user.sock.close()
      case SendMessageChat(user,message) =>
        this.messageAllUsers(user,message)
      case CheckUsersActivity() =>
        Future{this.checkUsersActivity()}
    }

    def checkUsersActivity(){
      while(true){
        for ((_, user) <- users){
          user ! doChat()
        }
        Thread.sleep(100)
      }
    }


    def messageAllUsers(fromUser: User, message: String) = {
      for((_, u) <- users){
        u ! ChatMessage(fromUser.name+" : "+message)
      }
    }

    def createUserActor(user: User, actorSystem: ActorSystem): ActorRef = {
     actorSystem.actorOf(Props(new UserActor(user.in, user.out, user.sock)),user.name)
    }

  }

}
