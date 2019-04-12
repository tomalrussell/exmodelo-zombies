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

    def flee: PartialFunction[Event, FleeZombie] = {
      case e: FleeZombie => e
    }

    def pursue: PartialFunction[Event, PursueHuman] = {
      case e: PursueHuman => e
    }
  }

  sealed trait Event
  case class Zombified(human: Human) extends Event
  case class Killed(zombie: Zombie) extends Event
  case class Rescued(human: Human) extends Event
  case class Gone(agent: Agent) extends Event
  case class FleeZombie(human: Human) extends Event
  case class PursueHuman(zombie: Zombie) extends Event
  case class Trapped(zombie: Zombie) extends Event

  sealed trait ArmyOption
  case object NoArmy extends ArmyOption
  case class Army(
    size: Int,
    fightBackProbability: Double = 1.0,
    exhaustionProbability: Double = physic.humanExhaustionProbability,
    perception: Double = physic.humanPerception,
    runSpeed: Double = physic.humanRunSpeed,
    followProbability: Double = physic.humanFollowProbability,
    maxRotation: Double = physic.humanMaxRotation,
    informProbability: Double = 0.0,
    aggressive: Boolean = true) extends ArmyOption

  sealed trait RedCrossOption
  case object NoRedCross extends RedCrossOption
  case class RedCross(
    size: Int,
    exhaustionProbability: Option[Double] = None,
    followProbability: Double = 0.0,
    informProbability: Double = physic.humanInformProbability,
    aggressive: Boolean = true,
    activationDelay: Int,
    efficiencyProbability: Double) extends RedCrossOption

  object Simulation {

    def initialize(
      world: World,
      infectionRange: Double = physic.infectionRange,
      humanRunSpeed: Double = physic.humanRunSpeed,
      humanPerception: Double = physic.humanPerception,
      humanMaxRotation: Double = physic.humanMaxRotation,
      humanExhaustionProbability: Double = physic.humanExhaustionProbability,
      humanFollowProbability: Double = physic.humanFollowProbability,
      humanInformedRatio: Double = physic.humanInformedRatio,
      humanInformProbability: Double = physic.humanInformProbability,
      humanFightBackProbability: Double = physic.humanFightBackProbability,
      humans: Int,
      zombieRunSpeed: Double = physic.zombieRunSpeed,
      zombiePerception: Double = physic.zombiePerception,
      zombieMaxRotation: Double = physic.zombieMaxRotation,
      zombiePheromone: PheromoneMechanism = physic.zombiePheromone,
      zombies: Int,
      walkSpeed: Double = physic.walkSpeed,
      rotationGranularity: Int = 5,
      army: ArmyOption = NoArmy,
      redCross: RedCrossOption = NoRedCross,
      random: Random) = {

      val cellSide = space.cellSide(world.side)


      def generateHuman = {
        val informed = random.nextDouble() < humanInformedRatio
        val rescue = Rescue(informed = informed, informProbability = humanInformProbability)
        Human.random(
          world = world,
          walkSpeed = walkSpeed * cellSide,
          runSpeed = humanRunSpeed * cellSide,
          exhaustionProbability = humanExhaustionProbability,
          perception = humanPerception * cellSide,
          maxRotation = humanMaxRotation,
          followRunningProbability = humanFollowProbability,
          fight = Fight(humanFightBackProbability),
          rescue = rescue,
          canLeave = true,
          rng = random)
      }

      def generateZombie = Zombie.random(world, walkSpeed * cellSide, zombieRunSpeed * cellSide, zombiePerception * cellSide, zombieMaxRotation, random)


      def generateSoldier(army: Army) = {
        val rescue = Rescue(informed = true, alerted = true, informProbability = army.informProbability)
        Human.random(
          world = world,
          walkSpeed = walkSpeed * cellSide,
          runSpeed = army.runSpeed * cellSide,
          exhaustionProbability = army.exhaustionProbability,
          perception = army.perception * cellSide,
          maxRotation = army.maxRotation,
          followRunningProbability = army.followProbability,
          fight = Fight(army.fightBackProbability, aggressive = army.aggressive),
          rescue = rescue,
          canLeave = false,
          rng = random)
      }

      def soldiers =
        army match {
          case NoArmy => Vector.empty
          case a: Army => Vector.fill(a.size)(generateSoldier(a))
        }

      def generateRedCrossVolunteers(redCross: RedCross) = {
        val rescue = Rescue(informProbability = redCross.informProbability, noFollow = true)
        val antidote = Antidote(activationDelay = redCross.activationDelay, efficiencyProbability = redCross.efficiencyProbability, exhaustionProbability = redCross.exhaustionProbability)
        Human.random(
          world = world,
          walkSpeed = walkSpeed * cellSide,
          runSpeed = humanRunSpeed * cellSide,
          exhaustionProbability = humanExhaustionProbability,
          perception = humanPerception * cellSide,
          maxRotation = humanMaxRotation,
          followRunningProbability = redCross.followProbability,
          fight = Fight(humanFightBackProbability, aggressive = redCross.aggressive),
          rescue = rescue,
          canLeave = false,
          antidote = antidote,
          rng = random)
      }

      def redCrossVolunteers =
        redCross match {
          case NoRedCross => Vector.empty
          case a: RedCross => Vector.fill(a.size)(generateRedCrossVolunteers(a))
        }

      val agents = Vector.fill(humans)(generateHuman) ++ Vector.fill(zombies)(generateZombie) ++ soldiers ++ redCrossVolunteers

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
        walkSpeed = walkSpeed * cellSide,
        zombiePheromone = zombiePheromone,
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
    walkSpeed: Double,
    zombiePheromone: PheromoneMechanism,
    rotationGranularity: Int)

  def step(step: Int, simulation: Simulation, neighborhoodCache: NeighborhoodCache, rng: Random) = {
    val index = Agent.index(simulation.agents, simulation.world.side)
    val w1 = Agent.releasePheromone(index, simulation.world, simulation.zombiePheromone)

    val (ai, infected, died) = Agent.fight(index, simulation.agents, simulation.infectionRange, Agent.zombify(_, _), rng)

    val (na1, moveEvents) =
      ai.map { a0 =>
        val ns = Agent.neighbors(index, a0, Agent.perception(a0), neighborhoodCache)

        val evolve =
          Agent.inform(ns, w1, rng) _ andThen
          Agent.alert(ns, rng) _ andThen
          Agent.takeAntidote _ andThen
          Agent.chooseRescue _ andThen
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

 type SimulationResult = (List[Simulation], List[Vector[Event]])


  def simulate(simulation: Simulation, rng: Random, steps: Int): SimulationResult = {
    def result(s: Simulation, events: Vector[Event], acc: SimulationResult) = (s :: acc._1, events :: acc._2)
    val (simulations, events) = simulate(simulation, rng, steps, result, (List(), List()))

    events.map(_.collect(Event.rescued).size).sum
    val timeForHalfRescued = events.map(_.collect(Event.rescued).size).tail.foldLeft(Seq(0))((acc, el)=> acc :+ (el + acc.last))
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

  object environment {
    def all = Vector(stadium, jaude)
    def stadium = world.World.stadium(15, 15, 5)
    def jaude = world.World.jaude
    def quarantine = world.World.quarantineStadium(15, 15)
    def square = world.World.square(15)
  }

  object physic {
    /* General parameters */
    val walkSpeed = 0.1
    val infectionRange = 0.32

    /* Human parameters */
    val humanPerception = 1.4
    val humanRunSpeed = 0.49
    val humanExhaustionProbability = 0.45
    val humanMaxRotation = 60
    val humanInformedRatio = 0.11
    val humanInformProbability = 0.09
    val humanFollowProbability = 0.27
    val humanFightBackProbability = 0.01

    /* Zombie parameters */
    val zombiePerception = 2.9
    val zombieRunSpeed = 0.28
    val zombiePheromone = Pheromone(evaporation = 0.38)
    val zombieMaxRotation = 30
  }

  def vigilence(world: World, humans: Int, zombies: Int, walkSpeed: Double, infectionRange: Double, rotation: Double, humanPerception: Double, humanInformedRatio: Double, humanAwarenessProbability: Double, zombiePerception: Double, random: Random, steps: Int): (List[Simulation], List[Vector[Event]]) = {

    val simulation =
      Simulation.initialize(
       world,
        infectionRange = infectionRange,
        humanRunSpeed = walkSpeed,
        humanExhaustionProbability = 1.0,
        humanPerception = humanPerception,
        humanMaxRotation = rotation,
        humanFollowProbability = 0.0,
        humanInformedRatio = humanInformedRatio,
        humanInformProbability = humanAwarenessProbability,
        humanFightBackProbability = 0.0,
        humans = humans,
        zombieRunSpeed = walkSpeed,
        zombiePerception = zombiePerception,
        zombieMaxRotation = rotation,
        zombies = zombies,
        walkSpeed = walkSpeed,
        zombiePheromone = NoPheromone,
        random = random
      )

    simulate(simulation, random, steps)
  }

}
