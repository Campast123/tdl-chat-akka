package actors

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{ServerSocket}

import actors.ChatActor.{ChatActor, CheckUsersActivity, Join, Logout, SendMessageChat}
import akka.actor.{ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ActorChatServer extends App{

  val actorSystem = ActorSystem("ServerActorSystem")
  val chatActor = actorSystem.actorOf(Props[ChatActor],"chatActor")
  chatActor ! CheckUsersActivity()
  checkConnections()

  def checkConnections(): Unit ={
    println("Creating server socket")
    val ss = new ServerSocket(4000)
    println("Created server socket")
    while(true){
      println("waiting accept server socket")
      val sock = ss.accept()
      println("accept server socket")
      val in = new BufferedReader(new InputStreamReader(sock.getInputStream))
      val out = new PrintStream(sock.getOutputStream)
      Future {
        out.println("What is your name?")
        val name = in.readLine()
        val user = User(name,sock,in,out)
        chatActor ! Join(user)
      }
    }
  }

}
