package zombies

import zombies.agent.Pheromone
import zombies.guitutils.controls._
import zombies.guitutils.parameters._
import zombies.simulation._
import zombies.world.World
import zombies.worldgen._

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
      def optionValue(o: Options) = controlValues.getOrElse(o.name, defaultOrOff(o)).toString


      val army = Army(
        size = value(armySize),
        fightBackProbability = value(armyFightBackProbability),
        exhaustionProbability = value(armyExhaustionProbability),
        value(armyPerception),
        value(armyRunSpeed),
        value(armyFollowProbability),
        value(armyMaxRotation),
        value(armyInformProbability))

      val worldsize = 40

      /*
      implicit val ctx: Ctx.Owner = Ctx.Owner.safe()

      def getController(name: String) = Rx{display.controllerSeq().find(_.name == name)}.now

      def getMecanismValue(name: String) = {
        getController(name).map(_.value.asInstanceOf[Mecanism]) match {
          case Some(v) => v
          case None =>
            println(s"$name not found")
            "random"
        }
      }
      */

      val genworld = world() match {
        case World.dummyWorld => World(GridGeneratorLauncher(
          optionValue(generationMethod),
          worldsize,
          value(randomDensity),
          value(expMixtureCenters),
          value(expMixtureRadius),
          value(expMixtureThreshold),
          value(blocksNumber),
          value(blocksMinSize),
          value(blocksMaxSize),
          value(percolationProba),
          value(percolationBordPoints),
          value(percolationLinkWidth)).getGrid(rng),worldsize)
        case w => w
      }

      Simulation.initialize(
        genworld,
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

