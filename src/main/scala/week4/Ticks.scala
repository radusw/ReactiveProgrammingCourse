package week4

import scala.concurrent.duration._
import scala.language.postfixOps
import rx.lang.scala.Observable
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.LockSupport

/**
 * @author radu
 */
object Ticks extends App {
  val ticks: Observable[Long] = Observable.interval(Duration(0.5, TimeUnit.SECONDS))
  val events = ticks.filter { _ % 2 == 0 }
  val bufs: Observable[Seq[Long]] = events.slidingBuffer(count = 2, skip = 1)

  val s = bufs.subscribe(println(_))
  Thread.sleep(3000)
  s.unsubscribe()

  val xs = Observable.from(List(3l, 2l, 1l))
  val yss = xs.map {
    x => Observable.interval(Duration(x, TimeUnit.SECONDS)).map { _ => s"|$x|" }.take(2)
  }
  val zs = yss.flatten

  val s2 = zs.subscribe(println(_))
  Thread.sleep(6500)
  s2.unsubscribe()
}