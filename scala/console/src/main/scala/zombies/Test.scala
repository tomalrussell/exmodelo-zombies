package zombies

import com.github.tomaslanger.chalk._
import zombies.world._
import zombies.agent._
import zombies.space._


import scala.util.Random

object Test extends App {

  val side = 40

  val infectionRange = 0.5 * cellSide(side)
  val humanVision = 0.7 * cellSide(side)
  val zombieVision = 1.0 * cellSide(side)
  val humanSpeed = 0.5 * cellSide(side)
  val zombieSpeed = 0.3 * cellSide(side)
  val zombieMaxRotation = 15
  val humanMaxRotation = 45

  val rng = new Random(42)
  val doorSize = 16
  val wallSize = (side - doorSize) / 2

//  val worldDescription =
//    s"""${"+" * wallSize}${"0" * doorSize}${"+" * wallSize}\n""" +
//      s"""+${"0" * (side - 2)}+\n""" * (wallSize - 1) +
//      s"""${"0" * side}\n""" * doorSize +
//      s"""+${"0" * (side - 2)}+\n""" * (wallSize - 1) +
//      s"""${"+" * wallSize}${"0" * doorSize}${"+" * wallSize}\n"""

  val worldDescription =
    s"""${"+" * side}\n""" +
      s"""+${"0" * (side - 2)}+\n""" * (side - 2) +
      s"""${"+" * side}\n"""

//  val worldDescription =
//    """+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |+++++++++++++++++++++++0000+++++++++++++
//      |++++++++++++++++00000000000+++++++++++++
//      |++++++++++++++++0000000000++++++++++++++
//      |++++++++++++++++0000000000++++++++++++++
//      |++++++++++++++++0000000000++++++++++++++
//      |++++++++++++++++0000000000++++++++++++++
//      |++++++++++++++++0000000000++++++++++++++
//      |++++++++++++++++0000000000++++++++++++++
//      |++++++++++++++++0000000000++++++++++++++
//      |0000000000000000000000000000000000000000
//      |0000000000000000000000000000000000000000
//      |0000000000000000000000000000000000000000
//      |0000000000000000000000000000000000000000
//      |++++++++++++++++00000000000+++++++++++++
//      |++++++++++++++++00000000000+++++++++++++
//      |++++++++++++++++00000000000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |++++++++++++++++++++++00000+++++++++++++
//      |""".stripMargin


  val world = World.computeSlope(World.computeAltitude(World.parse(worldDescription), 0.5), cellSide(side) * 0.2)
  val agents =
    Vector.fill(20)(Human.generate(world, humanSpeed, humanVision, humanMaxRotation,  rng)) ++
      Vector.fill(5)(Zombie.generate(world, zombieSpeed, zombieVision, zombieMaxRotation, rng))

  def clear(world: World) = {
    print(Ansi.cursorUp(world.side - 1))
    print(Ansi.cursorLeft(world.side))
  }

  def simulate(hs: Vector[Agent], world: World) = {
    val index = Agent.index(hs, world.side)
    val ai = Agent.infect(index, hs, infectionRange, Agent.zombify(_, zombieSpeed, zombieVision, zombieMaxRotation, rng))
    ai.map(Agent.adaptDirectionRotate(index, _, 5)).flatMap(Agent.move(_, world))
  }

  def step(hs: Vector[Agent], world: World): Unit = {
    if (!hs.isEmpty) {
      print(console.display(world, hs))
      val aim = simulate(hs, world)
      Thread.sleep(100)
      clear(world)
      step(aim, world)
    }
  }

  step(agents, world)

//  def bench(hs: Vector[Agent], world: World, steps: Int): Vector[Agent] = {
//   //println(steps)
//    if (steps == 0) hs else bench(simulate(hs, world), world, steps - 1)
//  }
//
//  val begin = System.currentTimeMillis()
//  bench(agents, world, 2000)
//  println(System.currentTimeMillis() - begin)

}
