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
  val simulation = Simulation.initialize(
    environment.stadium,
    humans = humans,
    zombies = zombies,
    walkSpeed = walkSpeed,
    random = rng
  )

  def display(simulation: Simulation, events: Vector[Event], allEvents: List[Event]) = {
    print(console.display(simulation, allEvents))
    Thread.sleep(100)
    console.clear(simulation)
    allEvents ++ events
  }

 // simulate(simulation, rng, 5000, display, List())

//  def bench(steps: Int) = simulate[Unit](simulation, rng, steps, (_, _, _) => Unit, Unit)
//
//  val begin = System.currentTimeMillis()
//  val end = bench(500)
//  println(System.currentTimeMillis() - begin)

}
