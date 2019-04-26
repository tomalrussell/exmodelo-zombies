package zombies

import scala.util.Random

object tools {

  sealed trait Logger

  case object Printer extends Logger
  case object DummyLogger extends Logger

  def log(msg: => String)(implicit logger: Logger) = {
    logger match {
      case Printer => println(msg)
      case DummyLogger =>
    }
  }

  def randomElement[T](v: Vector[T], rng: Random) = {
    val s = v.size
    if (s == 0) None
    else {
      val i = rng.nextInt(v.size)
      Some(v(i))
    }
  }

}
