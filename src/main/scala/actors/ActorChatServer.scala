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
    println("Creando server socket")
    val ss = new ServerSocket(4000)
    println("Server socket creado")
    while(true){
      println("Esperando accept server socket")
      val sock = ss.accept()
      println("Accept server socket")
      val in = new BufferedReader(new InputStreamReader(sock.getInputStream))
      val out = new PrintStream(sock.getOutputStream)
      Future {
        out.println("Cual es tu nombre?")
        val name = in.readLine()
        val user = User(name,sock,in,out)
        chatActor ! Join(user)
      }
    }
  }

}
