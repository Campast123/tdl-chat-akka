package actors

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{ServerSocket, Socket}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ChatClient extends App {
  println("Macking socket")
  val sock = new Socket("localhost", 4000)
  println("Socket made")
  val in = new BufferedReader(new InputStreamReader(sock.getInputStream))
  val out = new PrintStream(sock.getOutputStream)
  var stopped = false
  Future{
    while(!stopped){
      val p = in.readLine()
      if (p != null) println(p)
    }
  }
  var input = ""
  while (input != ":quit"){
    val input = readLine
    out.println(input)
  }
  sock.close()
  stopped = true
}
