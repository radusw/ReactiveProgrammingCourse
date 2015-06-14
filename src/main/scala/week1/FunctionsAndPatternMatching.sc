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
                                                  //| in$1$$anon$1@36d650da

  // ints map {x => x > 0}
  val bools = for (x <- ints) yield x >= 100000000//> bools  : week1.Generator[Boolean] = week1.Generator$$anon$3@25b80053

  bools.generate                                  //> res4: Boolean = false

  def pairs[T, U](t: Generator[U], u: Generator[T]) =
    for (x <- t; y <- u) yield (x, y)             //> pairs: [T, U](t: week1.Generator[U], u: week1.Generator[T])week1.Generator[(
                                                  //| U, T)]
  pairs(ints, ints).generate                      //> res5: (Int, Int) = (1612540751,-1662923248)

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

  lists.generate                                  //> res7: List[Int] = List(-2024951215, -1072415076, -964126538)

  trait Tree
  case class Inner(left: Tree, right: Tree) extends Tree
  case class Leaf(x: Int) extends Tree

  def leafs: Generator[Leaf] = for {
    x <- ints
  } yield Leaf(x)                                 //> leafs: => week1.Generator[week1.FunctionsAndPattenrMatching.Leaf]

  def inners: Generator[Inner] = for {
    l <- trees
    r <- trees
  } yield Inner(l, r)                             //> inners: => week1.Generator[week1.FunctionsAndPattenrMatching.Inner]

  def trees: Generator[Tree] = for {
    cutoff <- bools
    tree <- if (cutoff) leafs else inners
  } yield tree                                    //> trees: => week1.Generator[week1.FunctionsAndPattenrMatching.Tree]
  
  trees.generate                                  //> res8: week1.FunctionsAndPattenrMatching.Tree = Inner(Leaf(1489385933),Inner
                                                  //| (Leaf(-1856767617),Leaf(1730057328)))
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