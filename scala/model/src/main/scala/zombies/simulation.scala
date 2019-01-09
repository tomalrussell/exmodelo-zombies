package zombies

import agent._
import world._

import scala.util.Random

object simulation {

  object Simulation {

    def initialize(
      world: World,
      infectionRange: Double,
      humanSpeed: Double,
      humanPerception: Double,
      humanMaxRotation: Double,
      humans: Int,
      zombieSpeed: Double,
      zombiePerception: Double,
      zombieMaxRotation: Double,
      zombies: Int,
      minSpeed: Double,
      rotationGranularity: Int = 5,
      random: Random) = {

      val cellSide = space.cellSide(world.side)

      val agents =
        Vector.fill(humans)(Human.generate(world, humanSpeed * cellSide, humanPerception * cellSide, humanMaxRotation,  random)) ++
          Vector.fill(zombies)(Zombie.generate(world, zombieSpeed * cellSide, zombiePerception * cellSide, zombieMaxRotation, random))


      Simulation(
        world = world,
        agents = agents,
        infectionRange = infectionRange * cellSide,
        humanSpeed = humanSpeed * cellSide,
        humanPerception = humanPerception * cellSide,
        humanMaxRotation = humanMaxRotation,
        zombieSpeed = zombieSpeed * cellSide,
        zombiePerception = zombiePerception * cellSide,
        zombieMaxRotation = zombieMaxRotation,
        minSpeed = minSpeed * cellSide,
        rotationGranularity = rotationGranularity
      )

    }

    def parseWorld(worldDescription: String, altitudeDecay: Double = 0.25, slopeIntensity: Double = 0.01) =
      World.computeSlope(World.computeAltitude(World.parse(worldDescription), altitudeDecay), slopeIntensity)


  }

  case class Simulation(
    world: World,
    agents: Vector[Agent],
    infectionRange: Double,
    humanSpeed: Double,
    humanPerception: Double,
    humanMaxRotation: Double,
    zombieSpeed: Double,
    zombiePerception: Double,
    zombieMaxRotation: Double,
    minSpeed: Double,
    rotationGranularity: Int)

  def step(simulation: Simulation, neighborhoodCache: NeighborhoodCache, rng: Random) = {
    val index = Agent.index(simulation.agents, simulation.world.side)
    val ai = Agent.infect(index, simulation.agents, simulation.infectionRange, Agent.zombify(_, simulation.zombieSpeed, simulation.zombiePerception, simulation.zombieMaxRotation, rng))
    val newAgents = ai.map(Agent.adaptDirectionRotate(index, _, 5, neighborhoodCache)).flatMap(Agent.move(_, simulation.world, simulation.minSpeed))
    simulation.copy(agents = newAgents)
  }

  def simulate[T](simulation: Simulation, rng: Random, steps: Int, result: Simulation => T): List[T] = {
    val neighborhoodCache = World.visibleNeighborhoodCache(simulation.world, math.max(simulation.humanPerception, simulation.zombiePerception))

    def run0(steps: Int, acc: List[T]): List[T] =
      if(steps == 0) acc.reverse else {
        val s = step(simulation, neighborhoodCache, rng)
        run0(steps - 1, result(s) :: acc)
      }


    run0(steps, List())
  }

}
