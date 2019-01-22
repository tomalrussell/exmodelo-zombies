import scala.util.Random

package object zombies {

  def randomElement[T](v: Vector[T], rng: Random) = {
    val s = v.size
    if (s == 0) None
    else {
      val i = rng.nextInt(v.size)
      Some(v(i))
    }
  }

}
