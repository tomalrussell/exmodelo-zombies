package zombies

import zombies.simulation.{Event, SimulationResult}
import zombies.space.Position

object observable {

  def zombified (results : SimulationResult): Vector[Position] = {
      results._2.flatten.collect(Event.zombified).map{ case s => s.human.position }.toVector
  }

  def hitmap(results : SimulationResult, eventLocation: SimulationResult => Vector[Position]) = {
    val world = results._1.head.world
    val worldIntPos = eventLocation(results).map{ p => space.positionToLocation(p, world.side) }.groupBy(identity).mapValues(_.size)

    Array.tabulate[Int](world.side, world.side) { (x, y) => worldIntPos.getOrElse((x, y), 0) }

  }


}
