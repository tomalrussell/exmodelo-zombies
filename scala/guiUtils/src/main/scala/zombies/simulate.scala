package zombies

import zombies.guitutils.controls._
import zombies.guitutils.parameters._
import zombies.simulation._

import scala.util.Random

object simulate {

  def buildGUI(parameters: Parameter*): Unit = {

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
        world.World.jaude,
        infectionRange = controlValues.getOrElse(infectionRange.name, defaultOrOff(infectionRange)).asInstanceOf[Double],
        walkSpeed = controlValues.getOrElse(walkSpeed.name, defaultOrOff(walkSpeed)).asInstanceOf[Double],
        humanRunSpeed = controlValues.getOrElse(humanRunSpeed.name, defaultOrOff(humanRunSpeed)).asInstanceOf[Double],
        humanStamina = controlValues.getOrElse(humanStamina.name, defaultOrOff(humanStamina)).asInstanceOf[Int],
        humanPerception = controlValues.getOrElse(humanPerception.name, defaultOrOff(humanPerception)).asInstanceOf[Double],
        humanMaxRotation = controlValues.getOrElse(humanMaxRotation.name, defaultOrOff(humanMaxRotation)).asInstanceOf[Double],
        humans = controlValues.getOrElse(numberHumans.name, defaultOrOff(numberHumans)).asInstanceOf[Int],
        zombieRunSpeed = controlValues.getOrElse(zombieRunSpeed.name, defaultOrOff(zombieRunSpeed)).asInstanceOf[Double],
        zombieStamina = controlValues.getOrElse(zombieStamina.name, defaultOrOff(zombieStamina)).asInstanceOf[Int],
        zombiePerception = controlValues.getOrElse(zombiePerception.name, defaultOrOff(zombiePerception)).asInstanceOf[Double],
        zombieMaxRotation = controlValues.getOrElse(zombieMaxRotation.name, defaultOrOff(zombieMaxRotation)).asInstanceOf[Double],
        zombies = controlValues.getOrElse(numberZombies.name, defaultOrOff(numberZombies)).asInstanceOf[Int],
        rotationGranularity = controlValues.getOrElse(rotationGranularity.name, defaultOrOff(rotationGranularity)).asInstanceOf[Int],
        random = rng,
        humanFollowMode = agent.NoFollow //toFollowMode(controlValues.getOrElse(followMode.name, defaultOrOff(followMode)).asInstanceOf[Mecanism],
       // controlValues.getOrElse(followModeProbability.name, defaultOrOff(followModeProbability)).asInstanceOf[Double]
      )
    }

    display.init(() => initialize(rng), controlList)
  }

}

