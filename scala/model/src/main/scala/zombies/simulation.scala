package zombies

import agent._
import world._

import scala.util.Random

object simulation {

  object Event {
    def rescued : PartialFunction[Event, Rescued] = {
      case e: Rescued => e
    }

    def zombified: PartialFunction[Event, Zombified] = {
      case e: Zombified => e
    }

    def killed: PartialFunction[Event, Killed] = {
      case e: Killed => e
    }

    def gone: PartialFunction[Event, Gone] = {
      case e: Gone => e
    }

    def flee: PartialFunction[Event, Flee] = {
      case e: Flee => e
    }

    def pursue: PartialFunction[Event, Pursue] = {
      case e: Pursue => e
    }
  }

  sealed trait Event
  case class Zombified(human: Human) extends Event
  case class Killed(zombie: Zombie) extends Event
  case class Rescued(human: Human) extends Event
  case class Gone(agent: Agent) extends Event
  case class Flee(human: Human) extends Event
  case class Pursue(zombie: Zombie) extends Event


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
      zombiePheromonEvaporation: Double,
      zombies: Int,
      walkSpeed: Double,
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
        pheromonEvaporation = zombiePheromonEvaporation,
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
    humanExhaustionProbability: Double,
    humanFollowProbability: Double,
    zombieRunSpeed: Double,
    zombiePerception: Double,
    zombieMaxRotation: Double,
    zombieExhaustionProbability: Double,
    walkSpeed: Double,
    pheromonEvaporation: Double,
    rotationGranularity: Int)

  def step(step: Int, simulation: Simulation, neighborhoodCache: NeighborhoodCache, rng: Random) = {
    val index = Agent.index(simulation.agents, simulation.world.side)
    val w1 = Agent.pheromon(index, simulation.world, simulation.pheromonEvaporation)
    val (ai, infected, died) = Agent.fight(index, simulation.agents, simulation.infectionRange, Agent.zombify(_, _), rng)

    val (na1, moveEvents) =
      ai.map { a0 =>
        val ns = Agent.neighbors(index, a0, Agent.perception(a0), neighborhoodCache)

        val evolve =
          Agent.inform(ns, rng) _ andThen
          Agent.alert(ns, rng) _ andThen
          Agent.run(ns) _ andThen
          Agent.metabolism(rng) _

        val a1 = evolve(a0)

        val (a2, ev) = Agent.changeDirection(w1, simulation.rotationGranularity, ns, rng)(a1)

        Agent.move(w1, simulation.rotationGranularity, rng) (a2) match {
          case Some(a) => (Some(a), ev.toVector)
          case None => (None, Vector(Gone(a2)) ++ ev)
        }
      }.unzip

    val (na2, rescued) = Agent.rescue(w1, na1.flatten)

    val events =
      infected.map(i => Zombified(i)) ++
      died.map(d => Killed(d)) ++
      rescued.map(r => Rescued(r)) ++
      moveEvents.flatten

    (simulation.copy(agents = na2, world = w1), events)
  }




  def simulate(simulation: Simulation, rng: Random, steps: Int): (List[Simulation], List[Vector[Event]]) = {
    def result(s: Simulation, events: Vector[Event], acc: (List[Simulation], List[Vector[Event]])) = (s :: acc._1, events :: acc._2)
    val (simulations, events) = simulate(simulation, rng, steps, result, (List(), List()))
    (simulations.reverse, events.reverse)
  }

  def simulate[ACC](simulation: Simulation, rng: Random, steps: Int, accumulate: (Simulation, Vector[Event], ACC) => ACC, accumulator: ACC): ACC = {
    val neighborhoodCache = World.visibleNeighborhoodCache(simulation.world, math.max(simulation.humanPerception, simulation.zombiePerception))

    def run0(s: Int, simulation: Simulation, events: Vector[Event], r: (Simulation, Vector[Event], ACC) => ACC, accumulator: ACC): ACC =
      if(s == 0) r(simulation, events, accumulator)
      else {
        val newAccumulator = r(simulation, events, accumulator)
        val (newSimulation, newEvents) = step(steps - s, simulation, neighborhoodCache, rng)
        run0(s - 1, newSimulation, newEvents, r, newAccumulator)
      }

    run0(steps, simulation, Vector.empty, accumulate, accumulator)
  }

}
