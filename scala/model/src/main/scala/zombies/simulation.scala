package zombies

import agent._
import world._

import scala.util.Random

object simulation {

  object Simulation {

    def initialize(
                    world: World,
                    infectionRange: Double,
                    humanRunSpeed: Double,
                    humanPerception: Double,
                    humanMaxRotation: Double,
                    humanStamina: Int,
                    humanFollowProbability: Double,
                    humanInformedRatio: Double,
                    humanAwarenessProbability: Double,
                    humans: Int,
                    zombieRunSpeed: Double,
                    zombiePerception: Double,
                    zombieMaxRotation: Double,
                    zombieStamina: Int,
                    zombies: Int,
                    walkSpeed: Double,
                    rotationGranularity: Int = 5,
                    random: Random) = {

      val cellSide = space.cellSide(world.side)


      def generateHuman = {
        val informed = random.nextDouble() < humanInformedRatio
        val rescue = Rescue(informed = informed, perceiveInformation = humanAwarenessProbability)
        Human.random(world, walkSpeed * cellSide, humanRunSpeed * cellSide, humanStamina, humanPerception * cellSide, humanMaxRotation, humanFollowProbability, rescue, random)
      }

      def generateZombie = Zombie.random(world, walkSpeed * cellSide, zombieRunSpeed * cellSide, zombieStamina, zombiePerception * cellSide, zombieMaxRotation, random)

      val agents = Vector.fill(humans)(generateHuman) ++ Vector.fill(zombies)(generateZombie)

      Simulation(
        world = world,
        agents = agents,
        rescued = Vector.empty,
        infectionRange = infectionRange * cellSide,
        humanRunSpeed = humanRunSpeed * cellSide,
        humanPerception = humanPerception * cellSide,
        humanMaxRotation = humanMaxRotation,
        humanStamina = humanStamina,
        humanFollowProbability = humanFollowProbability,
        zombieRunSpeed = zombieRunSpeed * cellSide,
        zombiePerception = zombiePerception * cellSide,
        zombieMaxRotation = zombieMaxRotation,
        zombieStamina = zombieStamina,
        walkSpeed = walkSpeed * cellSide,
        rotationGranularity = rotationGranularity
      )

    }



  }

  case class Simulation(
    world: World,
    agents: Vector[Agent],
    rescued: Vector[Human],
    infectionRange: Double,
    humanRunSpeed: Double,
    humanPerception: Double,
    humanMaxRotation: Double,
    humanStamina: Int,
    humanFollowProbability: Double,
    zombieRunSpeed: Double,
    zombiePerception: Double,
    zombieMaxRotation: Double,
    zombieStamina: Int,
    walkSpeed: Double,
    rotationGranularity: Int)

  def step(simulation: Simulation, neighborhoodCache: NeighborhoodCache, rng: Random) = {
    val index = Agent.index(simulation.agents, simulation.world.side)
    val ai = Agent.infect(index, simulation.agents, simulation.infectionRange, Agent.zombify(_, _))

    val na1 =
      for { a0 <- ai } yield {
        val ns = Agent.neighbors(index, a0, Agent.vision(a0), neighborhoodCache)
        val a1 = Agent.inform(a0, ns, rng)
        val a2 = Agent.run(a1, ns)
        val a3 = Agent.metabolism(a2)
        val a4 = Agent.changeDirection(simulation.world, index, a3, simulation.rotationGranularity, ns, rng)
        Agent.move(a4, simulation.world, simulation.rotationGranularity, rng)
      }

    val (na2, rescued) = Agent.rescue(simulation.world, na1.flatten)
    simulation.copy(agents = na2, rescued = simulation.rescued ++ rescued)
  }

  def simulate[T](simulation: Simulation, rng: Random, steps: Int, result: Simulation => T): List[T] = {
    val neighborhoodCache = World.visibleNeighborhoodCache(simulation.world, math.max(simulation.humanPerception, simulation.zombiePerception))

    def run0(steps: Int, simulation: Simulation, acc: List[T]): List[T] =
      if(steps == 0) acc.reverse else {
        val s = step(simulation, neighborhoodCache, rng)
        run0(steps - 1, s, result(s) :: acc)
      }

    run0(steps, simulation, List())
  }

}
