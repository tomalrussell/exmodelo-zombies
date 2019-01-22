package zombies

import zombies.guitutils.controls._
import zombies.guitutils.parameters._
import zombies.simulation._

import scala.util.Random

object simulate {

  val defaulInfectionRange = 0.2
  val defaultWalkSpeed = 0.1
  val defaultHumanRunSpeed = 0.5
  val defaultHumanStamina = 10
  val defaultHumanPerception = 0.7
  val defaultHumanMaxRotation = 60.0
  val defaultNumberHumans = 250
  val defaultZombieRunSpeed = 0.3
  val defaultZombieStamina = 10
  val defaultZombiePerception = 1.2
  val defaultZombieMaxRotation = 45.0
  val defaultNumberZombies = 4
  val defaultRotationGranularity = 5
  val defaultFollowMode = NoFollowMode
  val defaultFollowModeProbability = 0.5

  val defaultAndOff = Seq(
    infectionRange-> (defaulInfectionRange, 0.0),
    walkSpeed-> (defaultWalkSpeed, 0.0),
    humanRunSpeed-> (defaultHumanRunSpeed, 0.0),
    humanPerception-> (defaultHumanPerception, 0.0),
    humanMaxRotation-> (defaultHumanMaxRotation, 0.0),
    humanStamina-> (defaultHumanStamina, 0),
    numberHumans-> (defaultNumberHumans, 0),
    zombieRunSpeed-> (defaultZombieRunSpeed, 0.0),
    zombiePerception-> (defaultZombiePerception, 0.0),
    zombieMaxRotation-> (defaultZombieMaxRotation, 0.0),
    zombieStamina-> (defaultZombieStamina, 0),
    numberZombies-> (defaultNumberZombies, 0),
    rotationGranularity-> (defaultRotationGranularity, 0),
    followMode-> (defaultFollowMode, NoFollowMode),
    followModeProbability-> (defaultFollowModeProbability, 1.0)
  ).toMap

  private val parameters = Seq(
    infectionRange -> Parameter(infectionRange, Doubles(0.0, 1.0, 0.1, defaulInfectionRange)),
    walkSpeed -> Parameter(walkSpeed, Doubles(0.0, 1.0, 0.1, defaultWalkSpeed)),
    humanRunSpeed -> Parameter(humanRunSpeed, Doubles(0.0, 1.0, 0.1, defaultHumanRunSpeed)),
    humanStamina -> Parameter(humanStamina, Ints(0, 50, 1, defaultHumanStamina)),
    humanPerception -> Parameter(humanPerception, Doubles(0.0, 5.0, 0.1, defaultHumanPerception)),
    humanMaxRotation -> Parameter(humanMaxRotation, Doubles(0.0, 180.0, 1.0, defaultHumanMaxRotation)),
    numberHumans -> Parameter(numberHumans, Ints(0, 1500, 1, defaultNumberHumans)),
    zombieRunSpeed -> Parameter(zombieRunSpeed, Doubles(0.0, 1.0, 0.1, defaultZombieRunSpeed)),
    zombieStamina -> Parameter(zombieStamina, Ints(0, 50, 1, defaultZombieStamina)),
    zombiePerception -> Parameter(zombiePerception, Doubles(0.0, 5.0, 0.1, defaultZombiePerception)),
    zombieMaxRotation -> Parameter(zombieMaxRotation, Doubles(0.0, 180.0, 1.0, defaultZombieMaxRotation)),
    numberZombies -> Parameter(numberZombies, Ints(0, 1500, 1, defaultNumberZombies)),
    rotationGranularity -> Parameter(rotationGranularity, Ints(0, 10, 1, defaultRotationGranularity)),
    followMode -> Parameter(followMode, Options(NoFollowMode, FollowRunning)),
    followModeProbability -> Parameter(followModeProbability, Doubles(0.0, 1.0, 0.1, defaultFollowModeProbability))
  )

  def buildGUI(variableParameters: Seq[ParameterName], parametersOff: Seq[ParameterName] = Seq()): Unit = {

    def defaultOrOff(parameterName: ParameterName) = {
      if (parametersOff.contains(parameterName)) defaultAndOff(parameterName)._2
      else defaultAndOff(parameterName)._1
    }


    val rng = new Random(42)

    val controlList = parameters.filter { p => variableParameters.contains(p._1) }.map { p => build(p._2) }.toSeq

    def initialize(rng: Random) = {


      val controlValues = controlList.map { p =>
        p.name -> p.value
      }.toMap

      Simulation.initialize(
        world.World.jaude,
        infectionRange = controlValues.getOrElse(infectionRange, defaultOrOff(infectionRange)).asInstanceOf[Double],
        walkSpeed = controlValues.getOrElse(walkSpeed, defaultOrOff(walkSpeed)).asInstanceOf[Double],
        humanRunSpeed = controlValues.getOrElse(humanRunSpeed, defaultOrOff(humanRunSpeed)).asInstanceOf[Double],
        humanStamina = controlValues.getOrElse(humanStamina, defaultOrOff(humanStamina)).asInstanceOf[Int],
        humanPerception = controlValues.getOrElse(humanPerception, defaultOrOff(humanPerception)).asInstanceOf[Double],
        humanMaxRotation = controlValues.getOrElse(humanMaxRotation, defaultOrOff(humanMaxRotation)).asInstanceOf[Double],
        humans = controlValues.getOrElse(numberHumans, defaultOrOff(numberHumans)).asInstanceOf[Int],
        zombieRunSpeed = controlValues.getOrElse(zombieRunSpeed, defaultOrOff(zombieRunSpeed)).asInstanceOf[Double],
        zombieStamina = controlValues.getOrElse(zombieStamina, defaultOrOff(zombieStamina)).asInstanceOf[Int],
        zombiePerception = controlValues.getOrElse(zombiePerception, defaultOrOff(zombiePerception)).asInstanceOf[Double],
        zombieMaxRotation = controlValues.getOrElse(zombieMaxRotation, defaultOrOff(zombieMaxRotation)).asInstanceOf[Double],
        zombies = controlValues.getOrElse(numberZombies, defaultOrOff(numberZombies)).asInstanceOf[Int],
        rotationGranularity = controlValues.getOrElse(rotationGranularity, defaultOrOff(rotationGranularity)).asInstanceOf[Int],
        random = rng,
        humanFollowMode = toFollowMode(controlValues.getOrElse(followMode, defaultOrOff(followMode)).asInstanceOf[Mecanism],
          controlValues.getOrElse(followModeProbability, defaultOrOff(followModeProbability)).asInstanceOf[Double])
      )
    }

    display.init(() => initialize(rng), controlList)
  }

}

