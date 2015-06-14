package week1

import sun.org.mozilla.javascript.ast.Yield

object FunctionsAndPattenrMatching {
  val f: String => String = { case "ping" => "pong" }
                                                  //> f  : String => String = <function1>
  f("ping")                                       //> res0: String = pong
  val g: PartialFunction[String, String] = { case "ping" => "pong" }
                                                  //> g  : PartialFunction[String,String] = <function1>
  g.isDefinedAt("a")                              //> res1: Boolean = false

  for {
    i <- 1 until 30 if i % 10 == 0;
    j <- i until 30 if j % 3 == 0
  } yield (i -> j)                                //> res2: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((10,12), (1
                                                  //| 0,15), (10,18), (10,21), (10,24), (10,27), (20,21), (20,24), (20,27))

  val list = List(Option("a"), Option(1), Option("g"), Option(12))
                                                  //> list  : List[Option[Any]] = List(Some(a), Some(1), Some(g), Some(12))
  for (Some(s: String) <- list) yield s           //> res3: List[String] = List(a, g)

  val ints = new Generator[Int] {
    def generate = scala.util.Random.nextInt()
  }                                               //> ints  : week1.Generator[Int] = week1.FunctionsAndPattenrMatching$$anonfun$ma
                                                  //| in$1$$anon$1@38d8415

  // ints map {x => x > 0}
  val bools = for (x <- ints) yield x >= 100000000//> bools  : week1.Generator[Boolean] = week1.Generator$$anon$3@37403a09

  bools.generate                                  //> res4: Boolean = true

  def pairs[T, U](t: Generator[U], u: Generator[T]) =
    for (x <- t; y <- u) yield (x, y)             //> pairs: [T, U](t: week1.Generator[U], u: week1.Generator[T])week1.Generator[(
                                                  //| U, T)]
  pairs(ints, ints).generate                      //> res5: (Int, Int) = (1483591554,-2083416121)

  def choose[T](lo: Int, hi: Int): Generator[Int] =
    for (x <- ints) yield lo + Math.abs(x) % (hi - lo)
                                                  //> choose: [T](lo: Int, hi: Int)week1.Generator[Int]
  def oneOf[T](xs: T*): Generator[T] =
    for (idx <- choose(0, xs.length)) yield xs(idx)
                                                  //> oneOf: [T](xs: T*)week1.Generator[T]

  oneOf("1", "2", "3").generate                   //> res6: String = 3

  def lists: Generator[List[Int]] = for {
    isEmpty <- bools
    list <- if (isEmpty) emtpyList(Nil) else nonEmptyList
  } yield list                                    //> lists: => week1.Generator[List[Int]]

  def emtpyList[T](x: T) = new Generator[T] {
    def generate = x
  }                                               //> emtpyList: [T](x: T)week1.Generator[T]
  def nonEmptyList = for {
    head <- ints
    tail <- lists
  } yield head :: tail                            //> nonEmptyList: => week1.Generator[List[Int]]

  lists.generate                                  //> res7: List[Int] = List(-1127596355, 353678996, -951611935, 131418361)
}

trait Generator[+T] {
  self => // an alias for 'this' <=> Generator.this.
  def generate: T

  def map[S](f: T => S): Generator[S] = new Generator[S] {
    def generate = f(self.generate)
  }

  def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
    def generate = f(self.generate).generate
  }
}