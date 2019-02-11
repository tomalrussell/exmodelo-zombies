package zombies.guitutils

import zombies.agent._
import zombies.simulation._
import zombies.guitutils.controls.Mecanism
import shapeless._

object parameters {

  type ParameterName = String

  object Parameter {
    def range: PartialFunction[Parameter, Range[_]] = {
      case r: Range[_] => r
    }
  }

  sealed trait Parameter

  case class Options(name: ParameterName, mecanisms: Seq[controls.Mecanism], default: Mecanism, off: Mecanism, activation: Activation) extends Parameter

  def defaultOrOff[T](parameter: Range[T]) = {
    if (parameter.activation == Off) parameter.value.off
    else parameter.value.default
  }

  trait Activation

  object Variable extends Activation

  object Off extends Activation

  object Default extends Activation

  object Range {
    val t = 9
    val caseInt = TypeCase[Range[Int]]
    val caseDouble = TypeCase[Range[Double]]
  }


  case class RangeValue[T](min: T, max: T, step: T, default: T, off: T)
  case class Range[T](name: ParameterName, value: RangeValue[T], activation: Activation) extends Parameter {
    def isDefault = copy(activation = Default)
    def asDefaultFrom(parameter: Range[T]) = parameter.copy(value = from(parameter), activation = Default)
    def isOff = copy(activation = Off)
    def from(aParameter: Range[T]) = aParameter.value
  }

  val numberZombies = Range("numberZombies", RangeValue(0, 1500, 1, 4, 0), Variable)
  val numberHumans = Range("numberHumans", RangeValue(0, 1500, 1, 250, 0), Variable)

  val infectionRange = Range("infectionRange", RangeValue(0.0, 1.0, 0.01, physic.infectionRange, 0.0), Variable)
  val walkSpeed = Range("walkSpeed", RangeValue(0.0, 1.0, 0.1, physic.walkSpeed, 0.0), Variable)

  val zombieRunSpeed = Range("zombieRunSpeed", RangeValue(0.0, 1.0, 0.01, physic.zombieRunSpeed, 0.0), Variable)
  val zombiePerception = Range("zombiePerception", RangeValue(0.0, 5.0, 0.01, physic.zombiePerception, 0.0), Variable)
  val zombieMaxRotation = Range("zombieMaxRotation", RangeValue(0.0, 180.0, 1.0, physic.zombieMaxRotation, 0.0), Variable)
  val zombiePheromoneEvaporation = Range("zombiePheromoneEvaporation", RangeValue(0.0, 10.0, 0.01, physic.zombiePheromone.evaporation, 0.0), Variable)

  val humanRunSpeed = Range("humanRunSpeed", RangeValue(0.0, 1.0, 0.01, physic.humanRunSpeed, 0.0), Variable)
  val humanPerception = Range("humanPerception", RangeValue(0.0, 5.0, 0.01, physic.humanPerception, 0.0), Variable)
  val humanMaxRotation = Range("humanMaxRotation", RangeValue(0.0, 180.0, 1.0, physic.humanMaxRotation, 0.0), Variable)
  val humanExhaustionProbability = Range("humanExhaustionProbability", RangeValue(0.0, 1.0, 0.01, physic.humanExhaustionProbability, 0.0), Variable)
  val humanFightBackProbability = Range("humanFightBackProbability", RangeValue(0.0, 1.0,  0.01, physic.humanFightBackProbability, 0.0), Variable)
  val humanFollowProbability = Range("humanFollowProbability", RangeValue(0.0, 1.0, 0.01, physic.humanFollowProbability, 0.0), Variable)
  val humanInformProbability = Range("humanInformProbability", RangeValue(0.0, 1.0, 0.01, physic.humanInformProbability, 0.05), Variable)
  val humanInformedRatio = Range("humanInformedRatio", RangeValue(0.0, 1.0, 0.01, physic.humanInformedRatio, 0.0), Variable)

  val armySize = Range("armySize", RangeValue(0, 1500, 1, 0, 0), Variable)
  val armyFightBackProbability = Range("armyFightBackProbability", RangeValue(0.0, 1.0,  0.01, 1.0, 0.0), Variable)
  val armyExhaustionProbability = Range("armyExhaustionProbability", RangeValue(0.0, 1.0, 0.01, physic.humanExhaustionProbability, 0.0), Variable)
  val armyPerception = Range("armyPerception", RangeValue(0.0, 5.0, 0.01, physic.humanPerception, 0.0), Variable)
  val armyRunSpeed = Range("armyRunSpeed", RangeValue(0.0, 1.0, 0.01, physic.humanRunSpeed, 0.0), Variable)
  val armyFollowProbability = Range("armyFollowProbability", RangeValue(0.0, 1.0, 0.01, physic.humanFollowProbability, 0.0), Variable)
  val armyMaxRotation = Range("armyMaxRotation", RangeValue(0.0, 180.0, 1.0, physic.humanMaxRotation, 0.0), Variable)
  val armyInformProbability = Range("armyInformProbability", RangeValue(0.0, 1.0, 0.01, 0.0, 0.05), Variable)

  //val army = Army(4, fightBackProbability = 0.99, exhaustionProbability = 0.1, perception = 4.0, runSpeed = 0.9, followRunning = 0.05, maxRotation = 180)

}