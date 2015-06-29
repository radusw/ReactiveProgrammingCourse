package week5

import akka.actor._
import akka.event.LoggingReceive

/**
 * -Dakka.loglevel=DEBUG -Dakka.actor.debug.receive=on
 * @author rgancea
 */
object Main extends App {
  val system = ActorSystem("MySystem")
  val t = system.actorOf(Props[TransferMain], name = "TransferMain")

  Thread.sleep(1000)
  system.shutdown()
}

class TransferMain extends Actor {
  val c1 = context.actorOf(Props[BankAccount], name = "Alice")
  val c2 = context.actorOf(Props[BankAccount], name = "Bob")

  c1 ! BankAccount.Deposit(100)

  def receive = LoggingReceive {
    case BankAccount.Done => transfer(50)
  }

  def transfer(amount: BigInt): Unit = {
    val b = context.actorOf(Props[Bank], name = "bank")
    b ! Bank.Transfer(c1, c2, amount)
    context.become(LoggingReceive {
      case Bank.Done =>
        println("success")
        context.stop(self)
    })
  }
}