package zombies

import zombies.world._
import zombies.agent._
import zombies.simulation._

import scala.util.Random

object Test extends App {


  val pheromonEvaporation = 0.05
  val walkSpeed = 0.1
  val infectionRange = 0.25

  val humanInformedRatio = 0.1
  val humanAwarenessProbability = 0.7
  val humanFollowProbability = 0.3
  val humanFightBackProbability = 0.2

  val humanPerception = 1.5
  val zombiePerception = 3.0

  val humanRunSpeed = 0.5
  val zombieRunSpeed = 0.3

  val humanExhaustionProbability = 0.45

  val zombieMaxRotation = 60
  val humanMaxRotation = 90

  val humans = 250
  val zombies = 10

  val rng = new Random(42)

  val world = World.jaude
  val noZombie = Simulation.initialize(
    World.jaude,
    infectionRange = infectionRange,
    humanRunSpeed = humanRunSpeed,
    humanExhaustionProbability = humanExhaustionProbability,
    humanPerception = humanPerception,
    humanMaxRotation = humanMaxRotation,
    humanFollowProbability = humanFollowProbability,
    humanInformedRatio = humanInformedRatio,
    humanAwarenessProbability = humanAwarenessProbability,
    humanFightBackProbability = humanFightBackProbability,
    humans = humans,
    zombieRunSpeed = zombieRunSpeed,
    zombiePerception = zombiePerception,
    zombieMaxRotation = zombieMaxRotation,
    zombies = zombies,
    walkSpeed = walkSpeed,
    zombiePheromonEvaporation = pheromonEvaporation,
    random = rng
  )

  val simulation = noZombie.copy(agents = noZombie.agents /*++ Seq(zombie(25))*/)
  val neighborhoodCache = World.visibleNeighborhoodCache(simulation.world, math.max(simulation.humanPerception, simulation.zombiePerception))

  def display(simulation: Simulation, events: Vector[Event], allEvents: List[Event]) = {
    print(console.display(simulation, allEvents))
    Thread.sleep(100)
    console.clear(simulation)
    allEvents ++ events
  }

  simulate(simulation, rng, 5000, display, List())



//  def bench(steps: Int) = simulate(simulation, rng, steps, _ => Unit)
//
//  val begin = System.currentTimeMillis()
//  val end = bench(500)
//  println(System.currentTimeMillis() - begin)

}
