package week5

import akka.actor._

object Actors extends App {
  val system = ActorSystem("MySystem")
  val l = system.actorOf(Props[Listener], name = "main")
  val c1 = system.actorOf(Props[Counter1], name = "counter1")
  val c2 = system.actorOf(Props[Counter2], name = "counter2")
  
  println(c1)

  c1 ! "inc"
  c1 ! "inc"
  c2 ! "inc"
  (c1 ! ("get"))(l)
  (c2 ! ("get"))(l)

  system.stop(c2)
  system.stop(c1)
  system.stop(l)
  
  Thread.sleep(1000)
  system.shutdown()
}

class Listener extends Actor {
  val c3 = context.actorOf(Props[Counter1], name = "counter1")
  c3 ! "inc"
  c3 ! "inc"
  c3 ! "inc"
  c3 ! "get"
  
  def receive = {
    case n: Int => println(s"count was $n")
  }
}

class Counter1 extends Actor with ActorLogging {
  private[this] var count = 0

  def receive = {
    case "inc" => count += 1
    case "get" => sender ! count
  }
}

class Counter2 extends Actor with ActorLogging {
  def counter(n: Int): Receive = {
    case "inc" => context.become(counter(n + 99))
    case "get" => sender ! n
  }
  def receive = counter(0)
}