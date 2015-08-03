package week4

import scala.concurrent._
import scala.concurrent.duration._

/**
 * @author radu
 */
object FutureExample extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
	val usdQuote = Future { 
    val id = Thread.currentThread().getId 
    println("usd - "+ id)
    Thread.sleep(2000)
    // throw new Exception("ex")
    id.toDouble
  }
	val chfQuote = Future { 
    val id = Thread.currentThread().getId 
    println("chf - " + id)
    Thread.sleep(1000)
    id.toDouble
  }
  
  println("main - " + Thread.currentThread().getId)
	val purchase = for {
		usd <- usdQuote
		chf <- chfQuote
		if isProfitable(usd, chf)
	} yield {
    println("one of the 2 futures threads - " + Thread.currentThread().getId)
    (usd+chf)/2
  }

	purchase onSuccess {
	  case amount => println("Purchased " + amount + " CHF")
	}

  def isProfitable(a: Double, b: Double) = a + b > 0
  
  
  Await.ready(purchase, 5 seconds)
}