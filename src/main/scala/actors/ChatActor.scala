package actors

import java.io.BufferedReader
import java.util.concurrent.ConcurrentHashMap

import actors.ActorChatServer.chatActor
import akka.actor.Actor
import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.JavaConverters._
import scala.concurrent.Future

object ChatActor {
  sealed trait Event
  final case class Join(user: User) extends Event
  final case class Logout(user: User) extends Event
  final case class SendMessageChat(user: User, message: String) extends Event
  final case class CheckUsersActivity() extends Event

  class ChatActor extends Actor{
    val users = new ConcurrentHashMap[String,User]().asScala

    def receive = {
      case Join(user) =>
        users += user.name -> user
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
        for ((name, user) <- users){
          doChat(user)
        }
        Thread.sleep(100)
      }
    }

    def nonBlockingRead(in: BufferedReader) : Option[String] = {
      if(in.ready()) Some(in.readLine()) else None
    }

    def doChat(user: User): Unit ={
      nonBlockingRead(user.in).foreach{ input =>
        if(input == ":quit"){
          chatActor ! Logout(user)
        } else {
          chatActor ! SendMessageChat(user,input)
        }
      }
    }

    def messageAllUsers(fromUser: User, message: String) = {
      for((n, u) <- users){
        u.out.println(fromUser.name+" : "+message)
      }
    }


  }

}
