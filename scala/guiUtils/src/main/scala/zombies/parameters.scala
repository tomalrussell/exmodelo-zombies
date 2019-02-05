package zombies.guitutils

import zombies.agent._
import zombies.simulation._
import zombies.guitutils.controls.Mecanism

object parameters {

  trait Value

  type ParameterName = String

  case class Doubles(min: Double, max: Double, step: Double, default: Double, off: Double) extends Value

  case class Ints(min: Int, max: Int, step: Double, default: Int, off: Int) extends Value

  case class Options(mecanisms: Seq[controls.Mecanism], default: Mecanism, off: Mecanism) extends Value

  case class Booleans(default: Boolean) extends Value

  def off(value: Value) = value match {
    case d: Doubles => d.off
    case i: Ints => i.off
    case m: Options => m.off
    case b: Booleans => false
  }

  def default(value: Value) = value match {
    case d: Doubles => d.default
    case i: Ints => i.default
    case m: Options => m.default
    case b: Booleans => b.default
  }

  trait Activation

  object Variable extends Activation

  object Off extends Activation

  object Default extends Activation


  case class Parameter(name: ParameterName, value: Value, activation: Activation) {

    def isDefault = copy(activation = Default)

    def asDefaultFrom(parameter: Parameter) = parameter.copy(value = from(parameter), activation = Default)

    def isOff = copy(activation = Off)

    def from(aParameter: Parameter) = value match {
      case d: Doubles =>
        val default = aParameter.value match {
          case d: Doubles => d.default
          case i: Ints => i.asInstanceOf[Double]
          case _ => throw new Throwable(s"The default value of parameter $aParameter cannot set to the parameter $this")
        }
        d.copy(default = default)
      case i: Ints =>
        val default = aParameter.value match {
          case i: Ints => i.default
          case _ => throw new Throwable(s"The default value of parameter $aParameter cannot set to the parameter $this")
        }
        i.copy(default = default)
      case _ => value
    }
  }

  val numberZombies = Parameter("numberZombies", Ints(0, 1500, 1, 4, 0), Variable)
  val numberHumans = Parameter("numberHumans", Ints(0, 1500, 1, 250, 0), Variable)

  val infectionRange = Parameter("infectionRange", Doubles(0.0, 1.0, 0.01, physic.infectionRange, 0.0), Variable)
  val walkSpeed = Parameter("walkSpeed", Doubles(0.0, 1.0, 0.1, physic.walkSpeed, 0.0), Variable)


  val zombieRunSpeed = Parameter("zombieRunSpeed", Doubles(0.0, 1.0, 0.01, physic.zombieRunSpeed, 0.0), Variable)
  val zombiePerception = Parameter("zombiePerception", Doubles(0.0, 5.0, 0.01, physic.zombiePerception, 0.0), Variable)
  val zombieMaxRotation = Parameter("zombieMaxRotation", Doubles(0.0, 180.0, 1.0, physic.zombieMaxRotation, 0.0), Variable)
  val zombiePheromoneEvaporation = Parameter("zombiePheromoneEvaporation", Doubles(0.0, 10.0, 0.01, physic.pheromonEvaporation, 0.0), Variable)


  val humanRunSpeed = Parameter("humanRunSpeed", Doubles(0.0, 1.0, 0.01, physic.humanRunSpeed, 0.0), Variable)
  val humanPerception = Parameter("humanPerception", Doubles(0.0, 5.0, 0.01, physic.humanPerception, 0.0), Variable)
  val humanMaxRotation = Parameter("humanMaxRotation", Doubles(0.0, 180.0, 1.0, physic.humanMaxRotation, 0.0), Variable)
  val humanExhaustionProbability = Parameter("humanExhaustionProbability", Doubles(0.0, 1.0, 0.01, physic.humanExhaustionProbability, 0.0), Variable)
  val humanFightBackProbability = Parameter("humanFightBackProbability", Doubles(0.0, 1.0,  0.01, physic.humanFightBackProbability, 0.0), Variable)
  val humanFollowProbability = Parameter("humanFollowProbability", Doubles(0.0, 1.0, 0.01, physic.humanFollowProbability, 0.0), Variable)
  val humanAwarenessProbability = Parameter("humanAwarenessProbability", Doubles(0.0, 1.0, 0.01, physic.humanAwarenessProbability, 0.05), Variable)


  val humanInformedRatio = Parameter("humanInformedRatio", Doubles(0.0, 1.0, 0.01, physic.humanInformedRatio, 0.0), Variable)



}