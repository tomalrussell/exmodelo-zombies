package zombies

import zombies.world._
import zombies.agent._
import zombies.simulation._

import scala.util.Random

object Test extends App {


  val pheromonEvaporation = 0.02
  val walkSpeed = 0.1
  val infectionRange = 0.2

  val humanInformedRatio = 0.1
  val humanAwarenessProbability = 0.05
  val humanFollowProbability = 0.2

  val humanPerception = 1.5
  val zombiePerception = 3.0

  val humanRunSpeed = 0.5
  val zombieRunSpeed = 0.3

  val humanExhaustionProbability = 0.05
  val zombieExhaustionProbability = 0.02

  val zombieMaxRotation = 45
  val humanMaxRotation = 90

  val humans = 250
  val zombies = 4

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
    humans = humans,
    zombieRunSpeed = zombieRunSpeed,
    zombieExhaustionProbability = zombieExhaustionProbability,
    zombiePerception = zombiePerception,
    zombieMaxRotation = zombieMaxRotation,
    zombies = zombies,
    walkSpeed = walkSpeed,
    pheromonEvaporation = pheromonEvaporation,
    random = rng
  )


//  def zombie(y: Double) = {
//    val cellSide = space.cellSide(world.side)
//    val speed = walkSpeed * cellSide
//    val position = (y * cellSide, 0.0)
//    val velocity = space.normalize((0.0, 1.0), speed)
//    Zombie(position, velocity, Speed(walkSpeed, zombieRunSpeed, zombieStamina, zombieStamina, false), zombiePerception * cellSide, zombieMaxRotation)
//  }

  val simulation = noZombie.copy(agents = noZombie.agents /*++ Seq(zombie(25))*/)
  val neighborhoodCache = World.visibleNeighborhoodCache(simulation.world, math.max(simulation.humanPerception, simulation.zombiePerception))

  def step(simulation: Simulation): Unit = {
    if (!simulation.agents.isEmpty) {
      print(console.display(simulation))
      val newState = _root_.zombies.simulation.step(simulation, neighborhoodCache, rng)
      Thread.sleep(100)
      console.clear(simulation)
      step(newState)
    }
  }

 step(simulation)

//  def bench(steps: Int) = simulate(simulation, rng, steps, _ => Unit)
//
//  val begin = System.currentTimeMillis()
//  val end = bench(1000)
//  println(System.currentTimeMillis() - begin)

}
