package zombies

import com.github.tomaslanger.chalk._
import zombies.world._
import zombies.agent._
import zombies.space._
import zombies.simulation._


import scala.util.Random

object Test extends App {

  val minSpeed = 0.1
  val infectionRange = 0.2

  val humanPerception = 1.5
  val zombiePerception = 3.0

  val humanSpeed = 0.5
  val zombieSpeed = 0.3

  val zombieMaxRotation = 45
  val humanMaxRotation = 90

  val humans = 250
  val zombies = 1

  val rng = new Random(42)

  val world = World.jaude
  val noZombie = Simulation.initialize(
    world,
    infectionRange = infectionRange,
    humanSpeed = humanSpeed,
    humanPerception = humanPerception,
    humanMaxRotation = humanMaxRotation,
    humans = humans,
    zombieSpeed = zombieSpeed,
    zombiePerception = zombiePerception,
    zombieMaxRotation = zombieMaxRotation,
    zombies = 0,
    minSpeed = minSpeed,
    random = rng
  )


  def zombie(y: Double) = {
    val cellSide = space.cellSide(world.side)
    val speed = zombieSpeed * cellSide
    val position = (y * cellSide, 0.0)
    val velocity = move.normalize((0.0, 1.0), speed)
    Zombie(position, velocity, speed, zombiePerception * cellSide, zombieMaxRotation)
  }

  val simulation = noZombie.copy(agents = noZombie.agents ++ Seq(zombie(25)))
  val neighborhoodCache = World.visibleNeighborhoodCache(simulation.world, math.max(simulation.humanPerception, simulation.zombiePerception))

  def step(simulation: Simulation): Unit = {
    if (!simulation.agents.isEmpty) {
      print(console.display(world, simulation.agents))
      val newState = _root_.zombies.simulation.step(simulation, neighborhoodCache, rng)
      Thread.sleep(100)
      console.clear(newState.world)
      step(newState)
    }
  }

 step(simulation)

//  def bench(hs: Vector[Agent], world: World, steps: Int): Vector[Agent] = {
//   //println(steps)
//    if (steps == 0) hs else bench(simulate(hs, world), world, steps - 1)
//  }
//
//  val begin = System.currentTimeMillis()
//  val end = bench(agents, world, 2000)
//  println(System.currentTimeMillis() - begin)

}
