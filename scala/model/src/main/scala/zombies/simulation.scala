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

      val agents =
        Vector.fill(humans)(Human.random(world, walkSpeed * cellSide, humanRunSpeed * cellSide, humanStamina, humanPerception * cellSide, humanMaxRotation,  random)) ++
          Vector.fill(zombies)(Zombie.random(world, walkSpeed * cellSide, zombieRunSpeed * cellSide, zombieStamina, zombiePerception * cellSide, zombieMaxRotation, random))

      Simulation(
        world = world,
        agents = agents,
        infectionRange = infectionRange * cellSide,
        humanRunSpeed = humanRunSpeed * cellSide,
        humanPerception = humanPerception * cellSide,
        humanMaxRotation = humanMaxRotation,
        humanStamina = humanStamina,
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
    infectionRange: Double,
    humanRunSpeed: Double,
    humanPerception: Double,
    humanMaxRotation: Double,
    humanStamina: Int,
    zombieRunSpeed: Double,
    zombiePerception: Double,
    zombieMaxRotation: Double,
    zombieStamina: Int,
    walkSpeed: Double,
    rotationGranularity: Int)

  def step(simulation: Simulation, neighborhoodCache: NeighborhoodCache, rng: Random) = {
    val index = Agent.index(simulation.agents, simulation.world.side)
    val ai = Agent.infect(index, simulation.agents, simulation.infectionRange, Agent.zombify(_, _))
    val newAgents = ai.map(Agent.metabolism).map(Agent.adaptDirectionRotate(simulation.world, index, _, simulation.rotationGranularity, neighborhoodCache, rng)).flatMap(Agent.move(_, simulation.world, simulation.rotationGranularity, rng))
    simulation.copy(agents = newAgents)
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
