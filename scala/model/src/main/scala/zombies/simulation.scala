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
      humanExhaustionProbability: Double,
      humanFollowProbability: Double,
      humanInformedRatio: Double,
      humanAwarenessProbability: Double,
      humanFightBackProbability: Double,
      humans: Int,
      zombieRunSpeed: Double,
      zombiePerception: Double,
      zombieMaxRotation: Double,
      zombieExhaustionProbability: Double,
      zombies: Int,
      walkSpeed: Double,
      pheromonEvaporation: Double,
      rotationGranularity: Int = 5,
      random: Random) = {

      val cellSide = space.cellSide(world.side)


      def generateHuman = {
        val informed = random.nextDouble() < humanInformedRatio
        val rescue = Rescue(informed = informed, awarenessProbability = humanAwarenessProbability)
        Human.random(
          world = world,
          walkSpeed = walkSpeed * cellSide,
          runSpeed = humanRunSpeed * cellSide,
          exhaustionProbability = humanExhaustionProbability,
          perception = humanPerception * cellSide,
          maxRotation = humanMaxRotation,
          followRunningProbability = humanFollowProbability,
          fightBackProbability = humanFightBackProbability,
          rescue = rescue,
          rng = random)
      }

      def generateZombie = Zombie.random(world, walkSpeed * cellSide, zombieRunSpeed * cellSide, zombieExhaustionProbability, zombiePerception * cellSide, zombieMaxRotation, random)

      val agents = Vector.fill(humans)(generateHuman) ++ Vector.fill(zombies)(generateZombie)

      Simulation(
        world = world,
        agents = agents,
        rescued = Vector.empty,
        infected = Vector.empty,
        died = Vector.empty,
        infectionRange = infectionRange * cellSide,
        humanRunSpeed = humanRunSpeed * cellSide,
        humanPerception = humanPerception * cellSide,
        humanMaxRotation = humanMaxRotation,
        humanExhaustionProbability = humanExhaustionProbability,
        humanFollowProbability = humanFollowProbability,
        zombieRunSpeed = zombieRunSpeed * cellSide,
        zombiePerception = zombiePerception * cellSide,
        zombieMaxRotation = zombieMaxRotation,
        zombieExhaustionProbability = zombieExhaustionProbability,
        walkSpeed = walkSpeed * cellSide,
        pheromonEvaporation = pheromonEvaporation,
        rotationGranularity = rotationGranularity
      )

    }



  }

  case class Simulation(
    world: World,
    agents: Vector[Agent],
    rescued: Vector[Human],
    infected: Vector[Human],
    died: Vector[Zombie],
    infectionRange: Double,
    humanRunSpeed: Double,
    humanPerception: Double,
    humanMaxRotation: Double,
    humanExhaustionProbability: Double,
    humanFollowProbability: Double,
    zombieRunSpeed: Double,
    zombiePerception: Double,
    zombieMaxRotation: Double,
    zombieExhaustionProbability: Double,
    walkSpeed: Double,
    pheromonEvaporation: Double,
    rotationGranularity: Int)

  def step(simulation: Simulation, neighborhoodCache: NeighborhoodCache, rng: Random) = {
    val index = Agent.index(simulation.agents, simulation.world.side)
    val w1 = Agent.pheromon(index, simulation.world, simulation.pheromonEvaporation)
    val (ai, infected, died) = Agent.fight(index, simulation.agents, simulation.infectionRange, Agent.zombify(_, _), rng)

    val na1 =
      for { a0 <- ai } yield {
        val ns = Agent.neighbors(index, a0, Agent.perception(a0), neighborhoodCache)

        val evolve =
          Agent.inform(ns, rng) _ andThen
          Agent.alert(ns, rng) _ andThen
          Agent.run(ns) _ andThen
          Agent.metabolism(rng) _ andThen
          Agent.changeDirection(w1, index, simulation.rotationGranularity, ns, rng) _

        val a1 = evolve(a0)

        Agent.move(w1, simulation.rotationGranularity, rng) (a1)
      }

    val (na2, rescued) = Agent.rescue(w1, na1.flatten)
    simulation.copy(
      agents = na2,
      rescued = simulation.rescued ++ rescued,
      infected = simulation.infected ++ infected,
      died = simulation.died ++ died,
      world = w1)
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
