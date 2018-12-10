package actors

import java.util.concurrent.ConcurrentHashMap

import actors.UserActor.{ListenNewInput, PrintMessage, UserActor}
import akka.actor.{Actor, ActorRef, Props}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ChatActor {
  sealed trait Event
  final case class Join(user: User) extends Event
  final case class Logout(user: User) extends Event
  final case class SendMessageChat(user: User, message: String) extends Event
  final case class CheckUsersActivity() extends Event

  class ChatActor extends Actor{
    val users = new ConcurrentHashMap[String,ActorRef]().asScala

    def receive = {
      case Join(user) =>
        users += user.name -> this.createUserActor(user)
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
          user ! ListenNewInput()
        }
        Thread.sleep(100)
      }
    }


    def messageAllUsers(fromUser: User, message: String) = {
      for((_, u) <- users){
        u ! PrintMessage(fromUser.name+" : "+message)
      }
    }

    def createUserActor(user: User): ActorRef = {
     context.actorOf(Props(new UserActor(user)),user.name)
    }

  }

}
