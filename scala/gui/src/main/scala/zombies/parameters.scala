package zombies

object parameters {

  trait ParameterType

  case class Doubles(min: Double, max: Double, step: Double, default: Double) extends ParameterType
  case class Ints(min: Int, max: Int, step: Double, default: Int) extends ParameterType
  case class Booleans(default: Boolean) extends ParameterType

  case class Parameter(name: String, parameterType: ParameterType)

  val list = Seq(
    Parameter("infectionRange", Doubles(0.0, 1.0, 0.1, 0.2)),
    Parameter("humanSpeed", Doubles(0.0, 5.0, 0.1, 0.5)),
    Parameter("humanPerception", Doubles(0.0, 5.0, 0.1, 0.7)),
    Parameter("humanMaxRotation", Doubles(0.0, 180.0, 1.0, 60.0)),
    Parameter("# humans", Ints(0, 1500, 1, 250)),
    Parameter("zombieSpeed", Doubles(0.0, 5.0, 0.1, 0.3)),
    Parameter("zombiePerception", Doubles(0.0, 5.0, 0.1, 1.2)),
    Parameter("zombieMaxRotation", Doubles(0.0, 180.0, 1.0, 45.0)),
    Parameter("# zombies", Ints(0, 1500, 1, 4)),
    Parameter("minSpeed", Doubles(0.0, 5.0, 0.1, 0.1)),
    Parameter("rotationGranularity", Ints(0, 10, 1, 5)),
  )
}