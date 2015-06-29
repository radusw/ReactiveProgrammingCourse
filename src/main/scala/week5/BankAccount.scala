package week5

import akka.actor._
import akka.event.LoggingReceive

/**
 * @author rgancea
 */
class BankAccount extends Actor {
  import BankAccount._

  private[this] var balance = BigInt(0)

  def receive = LoggingReceive {
    case Deposit(amount) =>
      balance += amount 
      sender ! Done
    case Withdraw(amount) if (amount <= balance) => 
      balance -= amount 
      sender ! Done
    case _ => sender ! Failed
  }
}

object BankAccount {
  case class Deposit(amount: BigInt) {
    require(amount > 0)
  }
  case class Withdraw(amount: BigInt) {
    require(amount > 0)
  }
  case object Done
  case object Failed
}