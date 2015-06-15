package week1

object monads {
  // monoid
  1 + (2 + 3)                                     //> res0: Int = 6

  // bind == flatMap
  Some(5) flatMap { x => Some(x * 2) }            //> res1: Option[Int] = Some(10)
  Some(2) map { x => x * 2 }                      //> res2: Option[Int] = Some(4)

  val strs = List("1", "two", "3", "four", "five")//> strs  : List[String] = List(1, two, 3, four, five)
  val nums = strs.flatMap { s =>
    try List(s.toInt) catch {
      case _: NumberFormatException => Nil
    }
  }                                               //> nums  : List[Int] = List(1, 3)
  nums == List(1, 3)                              //> res3: Boolean = true
}