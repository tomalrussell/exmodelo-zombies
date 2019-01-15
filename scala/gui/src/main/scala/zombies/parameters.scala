package zombies

import zombies.agent.FollowMode
import zombies.controls.Mecanism

object parameters {

  trait ParameterType

  case class Doubles(min: Double, max: Double, step: Double, default: Double) extends ParameterType

  case class Ints(min: Int, max: Int, step: Double, default: Int) extends ParameterType

  case class Options(mecanisms: Mecanism*) extends ParameterType

  case class Booleans(default: Boolean) extends ParameterType

  case class Parameter(name: ParameterName, parameterType: ParameterType)

  type ParameterName = String

  val infectionRange: ParameterName = "infectionRange"
  val walkSpeed: ParameterName = "walkSpeed"
  val humanRunSpeed: ParameterName = "humanRunSpeed"
  val humanStamina: ParameterName = "humanStamina"
  val humanPerception: ParameterName = "humanPerception"
  val humanMaxRotation: ParameterName = "humanMaxRotation"
  val numberHumans: ParameterName = "# humans"
  val zombieRunSpeed: ParameterName = "zombieRunSpeed"
  val zombieStamina:ParameterName = "zombieStamina"
  val zombiePerception:ParameterName = "zombieStamina"
  val zombieMaxRotation: ParameterName = "zombieMaxRotation"
  val numberZombies : ParameterName = "numberZombies"
  val rotationGranularity : ParameterName = "rotationGranularity"
  val followMode : ParameterName = "followMode"
  val followModeProbability: ParameterName = "followMode probability"

  val list = Seq(
    Parameter(infectionRange, Doubles(0.0, 1.0, 0.1, 0.2)),
    Parameter(walkSpeed, Doubles(0.0, 1.0, 0.1, 0.1)),
    Parameter(humanRunSpeed, Doubles(0.0, 1.0, 0.1, 0.5)),
    Parameter(humanStamina, Ints(0, 50, 1, 10)),
    Parameter(humanPerception, Doubles(0.0, 5.0, 0.1, 0.7)),
    Parameter(humanMaxRotation, Doubles(0.0, 180.0, 1.0, 60.0)),
    Parameter(numberHumans, Ints(0, 1500, 1, 250)),
    Parameter(zombieRunSpeed, Doubles(0.0, 1.0, 0.1, 0.3)),
    Parameter(zombieStamina, Ints(0, 50, 1, 10)),
    Parameter(zombiePerception, Doubles(0.0, 5.0, 0.1, 1.2)),
    Parameter(zombieMaxRotation, Doubles(0.0, 180.0, 1.0, 45.0)),
    Parameter(numberZombies, Ints(0, 1500, 1, 4)),
    Parameter(rotationGranularity, Ints(0, 10, 1, 5)),
    Parameter(followMode, Options(controls.FollowMode, controls.FollowRunning)),
    Parameter(followModeProbability, Doubles(0.0, 1.0, 0.1, 0.5))
  )
}