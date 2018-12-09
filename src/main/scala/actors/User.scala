package actors

import java.io.{BufferedReader, PrintStream}
import java.net.Socket

case class User(name: String, sock: Socket, in: BufferedReader, out: PrintStream)

