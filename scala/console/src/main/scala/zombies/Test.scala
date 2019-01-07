package zombies

import com.github.tomaslanger.chalk._
import zombies.world._
import zombies.agent._
import zombies.space._
import zombies.simulation._


import scala.util.Random

object Test extends App {

  val side = 40

  val minSpeed = 0.1 * cellSide(side)
  val infectionRange = 0.2 * cellSide(side)

  val humanPerception = 1.5 * cellSide(side)
  val zombiePerception = 3.0 * cellSide(side)

  val humanSpeed = 0.5 * cellSide(side)
  val zombieSpeed = 0.3 * cellSide(side)

  val zombieMaxRotation = 45
  val humanMaxRotation = 60

  val humans = 250
  val zombies = 4

  val rng = new Random(42)
  val doorSize = 16
  val wallSize = (side - doorSize) / 2

//  val worldDescription =
//    s"""${"+" * wallSize}${"0" * doorSize}${"+" * wallSize}\n""" +
//      s"""+${"0" * (side - 2)}+\n""" * (wallSize - 1) +
//      s"""${"0" * side}\n""" * doorSize +
//      s"""+${"0" * (side - 2)}+\n""" * (wallSize - 1) +
//      s"""${"+" * wallSize}${"0" * doorSize}${"+" * wallSize}\n"""

//  val worldDescription =
//    s"""${"+" * side}\n""" +
//      s"""+${"0" * (side - 2)}+\n""" * (side - 2) +
//      s"""${"+" * side}\n"""

  val worldDescription =
    """+++++++00000+++++++++++0000+++++++++++++
      |+++++++00000+++++++++++0000+++++++++++++
      |+++++++00000+++++++++++0000+++++++++++++
      |+++++++00000+++++++++++0000+++++++++++++
      |+++++++00000+++++++++++0000+++++++++++++
      |++++0000000000000++++++0000+++++++++++++
      |++++0000000000000++++++00000000000000000
      |++++0000000000000++++++00000000000000000
      |++++0000000000000++++++00000000000000000
      |++++0000000000000++++++0000+++++++++++++
      |++++0000000000000++++++0000+++++++++++++
      |++++0000000000000++++++0000+++++++++++++
      |++++0000000000000++++++0000+++++++++++++
      |++++++++++++00000++++++0000+++++++++++++
      |++++++++++++000000000000000+++++++++++++
      |++++++++++++00000000000000++++++++++++++
      |++++++++++++++++0000000000++++++++++++++
      |++++++++++++++++0000000000++++++++++++++
      |++++++++++++++++0000000000++++++++++++++
      |++++++++++++++++0000000000++++++++++++++
      |++++++++++++++++0000000000++++++++++++++
      |++++++++++++++++0000000000++++++++++++++
      |0000000000000000000000000000000000000000
      |0000000000000000000++++00000000000000000
      |0000000000000000000++++00000000000000000
      |0000000000000000000000000000000000000000
      |++++++++++++++++00000000000+++++++++++++
      |++++++++++++++++00000000000+++++++++++++
      |++++++++++++++++00000000000+++++++++++++
      |++++++++++++++++++++++00000+++++++++++++
      |++++++++++++++++++++++00000+++++++++++++
      |+++++0000000000000++++00000+++++++++++++
      |+++++0000000000000++++00000+++++++++++++
      |+++++0000000000000++++00000+++++++++++++
      |+++++0000000000000000000000+++++++++++++
      |+++++0000000000000000000000+++++++++++++
      |+++++0000000000000000000000+++++++++++++
      |+++++0000000000000000000000+++++++++++++
      |+++++0000000000000++++00000+++++++++++++
      |++++++++++++++++++++++00000+++++++++++++
      |""".stripMargin


  val world = Simulation.parseWorld(worldDescription)
  val simulation = Simulation.initialize(
    world,
    infectionRange = infectionRange,
    humanSpeed = humanSpeed,
    humanPerception = humanPerception,
    humanMaxRotation = humanMaxRotation,
    humans = humans,
    zombieSpeed = zombieSpeed,
    zombiePerception = zombiePerception,
    zombieMaxRotation = zombieMaxRotation,
    zombies = zombies,
    minSpeed = minSpeed,
    random = rng
  )

  def step(simulation: Simulation): Unit = {
    if (!simulation.agents.isEmpty) {
      print(console.display(world, simulation.agents))
      val newState = simulate(simulation, rng)
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
