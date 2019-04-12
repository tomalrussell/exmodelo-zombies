package zombies

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

}
