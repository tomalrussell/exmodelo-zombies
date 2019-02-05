package zombies

import zombies.world._
import zombies.agent._
import zombies.simulation._

import scala.util.Random

object Test extends App {

  import physic._

  val humans = 250
  val zombies = 10

  val rng = new Random(42)

  val world = World.jaude
  val noZombie = Simulation.initialize(
    environment.stadium,
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

 // simulate(simulation, rng, 5000, display, List())



  def bench(steps: Int) = simulate[Unit](simulation, rng, steps, (_, _, _) => Unit, Unit)

  val begin = System.currentTimeMillis()
  val end = bench(500)
  println(System.currentTimeMillis() - begin)

}
