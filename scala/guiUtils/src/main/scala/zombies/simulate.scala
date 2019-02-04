package zombies

import zombies.guitutils.controls._
import zombies.guitutils.parameters._
import zombies.simulation._
import zombies.world.World

import scala.util.Random

object simulate {

  def buildGUI(world: () => World, parameters: Parameter*): Unit = {

    def defaultOrOff(parameter: Parameter) = {
      if (parameter.activation == Off) off(parameter.value)
      else default(parameter.value)
    }

    val rng = new Random(42)

    val controlList = parameters.filter { p => p.activation == Variable }.map { p => build(p) }.toSeq

    def initialize(rng: Random) = {

      val controlValues = controlList.map { p =>
        p.name -> p.value
      }.toMap

      Simulation.initialize(
        world(),
        infectionRange = controlValues.getOrElse(infectionRange.name, defaultOrOff(infectionRange)).asInstanceOf[Double],
        walkSpeed = controlValues.getOrElse(walkSpeed.name, defaultOrOff(walkSpeed)).asInstanceOf[Double],
        humanRunSpeed = controlValues.getOrElse(humanRunSpeed.name, defaultOrOff(humanRunSpeed)).asInstanceOf[Double],
        humanExhaustionProbability = controlValues.getOrElse(humanExhaustionProbability.name, defaultOrOff(humanExhaustionProbability)).asInstanceOf[Double],
        humanFightBackProbability = controlValues.getOrElse(humanFightBackProbability.name, defaultOrOff(humanFightBackProbability)).asInstanceOf[Double],
        humanFollowProbability = controlValues.getOrElse(humanFollowProbability.name, defaultOrOff(humanFollowProbability)).asInstanceOf[Double],
        humanInformedRatio = controlValues.getOrElse(humanInformedRatio.name, defaultOrOff(humanInformedRatio)).asInstanceOf[Double],
        humanAwarenessProbability = controlValues.getOrElse(humanAwarenessProbability.name, defaultOrOff(humanAwarenessProbability)).asInstanceOf[Double],
        humanPerception = controlValues.getOrElse(humanPerception.name, defaultOrOff(humanPerception)).asInstanceOf[Double],
        humanMaxRotation = controlValues.getOrElse(humanMaxRotation.name, defaultOrOff(humanMaxRotation)).asInstanceOf[Double],
        humans = controlValues.getOrElse(numberHumans.name, defaultOrOff(numberHumans)).asInstanceOf[Int],
        zombieRunSpeed = controlValues.getOrElse(zombieRunSpeed.name, defaultOrOff(zombieRunSpeed)).asInstanceOf[Double],
        zombiePerception = controlValues.getOrElse(zombiePerception.name, defaultOrOff(zombiePerception)).asInstanceOf[Double],
        zombieMaxRotation = controlValues.getOrElse(zombieMaxRotation.name, defaultOrOff(zombieMaxRotation)).asInstanceOf[Double],
        zombiePheromonEvaporation = controlValues.getOrElse(zombiePheromonEvaporation.name, defaultOrOff(zombiePheromonEvaporation)).asInstanceOf[Double],
        zombies = controlValues.getOrElse(numberZombies.name, defaultOrOff(numberZombies)).asInstanceOf[Int],
        rotationGranularity = controlValues.getOrElse(rotationGranularity.name, defaultOrOff(rotationGranularity)).asInstanceOf[Int],
        random = rng
      )
    }

    display.init(() => initialize(rng), controlList)
  }

}

