package actors

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{ServerSocket, Socket}
import java.util.concurrent.ConcurrentHashMap

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._
object ChatServer extends App{
  case class User(name: String, sock: Socket, in: BufferedReader, out: PrintStream)
  val users = new ConcurrentHashMap[String,User]().asScala
  Future { checkConnections()}
  while(true){
    for ((name, user) <- users){
      doChat(user)
    }
    Thread.sleep(100)
  }

  def checkConnections(): Unit ={
    val ss = new ServerSocket(4000)
    while(true){
      val sock = ss.accept()
      val in = new BufferedReader(new InputStreamReader(sock.getInputStream))
      val out = new PrintStream(sock.getOutputStream)
      Future {
        out.println("What is your name?")
        val name = in.readLine()
        val user = User(name,sock,in,out)
        users += name -> user
      }
    }
  }

  def nonBlockingRead(in: BufferedReader) : Option[String] = {
    if(in.ready()) Some(in.readLine()) else None
  }

  def doChat(user: User): Unit ={
    nonBlockingRead(user.in).foreach{ input =>
      if(input == ":quit"){
        user.sock.close()
        users -= user.name
      } else {
        for((n, u) <- users){
          u.out.println(user.name+" : "+input)
        }
      }
    }
  }

}

