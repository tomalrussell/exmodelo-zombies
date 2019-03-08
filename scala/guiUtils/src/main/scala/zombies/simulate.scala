package zombies

import zombies.agent.Pheromone
import zombies.guitutils.controls._
import zombies.guitutils.parameters._
import zombies.simulation._
import zombies.world.World

import scala.util.Random

object simulate {

  def buildGUI(world: () => World, parameters: Parameter*): Unit = {


    val rng = new Random(42)

//    val controlList = parameters.collect(Parameter.range).filter { p => p.activation == Variable }.map { p => build(p) }.toSeq

    val controlList = parameters.filter { p => p.activation == Variable }.map { p => build(p) }

    def initialize(rng: Random) = {

      val controlValues =
        controlList.map { p =>
          p.name -> p.value
        }.toMap

      def value[T](p: Range[T]) = controlValues.getOrElse(p.name, defaultOrOff(p)).asInstanceOf[T]

      val army = Army(
        size = value(armySize),
        fightBackProbability = value(armyFightBackProbability),
        exhaustionProbability = value(armyExhaustionProbability),
        value(armyPerception),
        value(armyRunSpeed),
        value(armyFollowProbability),
        value(armyMaxRotation),
        value(armyInformProbability))

      Simulation.initialize(
        world(),
        infectionRange = value(infectionRange),
        walkSpeed = value(walkSpeed),
        humanRunSpeed = value(humanRunSpeed),
        humanExhaustionProbability = value(humanExhaustionProbability),
        humanFightBackProbability = value(humanFightBackProbability),
        humanFollowProbability = value(humanFollowProbability),
        humanInformedRatio = value(humanInformedRatio),
        humanInformProbability = value(humanInformProbability),
        humanPerception = value(humanPerception),
        humanMaxRotation = value(humanMaxRotation),
        humans = value(numberHumans),
        zombieRunSpeed = value(zombieRunSpeed),
        zombiePerception = value(zombiePerception),
        zombieMaxRotation = value(zombieMaxRotation),
        zombiePheromone = Pheromone(evaporation = value(zombiePheromoneEvaporation)),
        zombies = value(numberZombies),
        army = army,
        random = rng
      )
    }

    display.init(() => initialize(rng), controlList)
  }

}

