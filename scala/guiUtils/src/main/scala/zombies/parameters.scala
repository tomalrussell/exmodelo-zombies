package zombies.guitutils

import zombies.agent._
import zombies.guitutils.controls.Mecanism

object parameters {

  trait ParameterType

  type ParameterName = String

  case class Doubles(min: Double, max: Double, step: Double, default: Double) extends ParameterType

  case class Ints(min: Int, max: Int, step: Double, default: Int) extends ParameterType

  case class Options(mecanisms: controls.Mecanism*) extends ParameterType

  case class Booleans(default: Boolean) extends ParameterType

  case class Parameter(name: ParameterName, parameterType: ParameterType)


  val infectionRange: ParameterName = "infectionRange"
  val walkSpeed: ParameterName = "walkSpeed"
  val humanRunSpeed: ParameterName = "humanRunSpeed"
  val humanStamina: ParameterName = "humanStamina"
  val humanPerception: ParameterName = "humanPerception"
  val humanMaxRotation: ParameterName = "humanMaxRotation"
  val numberHumans: ParameterName = "# humans"
  val zombieRunSpeed: ParameterName = "zombieRunSpeed"
  val zombieStamina: ParameterName = "zombieStamina"
  val zombiePerception: ParameterName = "zombieStamina"
  val zombieMaxRotation: ParameterName = "zombieMaxRotation"
  val numberZombies: ParameterName = "numberZombies"
  val rotationGranularity: ParameterName = "rotationGranularity"
  val followMode: ParameterName = "followMode"
  val followModeProbability: ParameterName = "followMode probability"
}