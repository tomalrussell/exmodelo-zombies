package zombies

import zombies.agent.Agent
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
  
  def humansDynamic(results : SimulationResult) = {
    val (simulations, _) = results
    simulations.take(1).map(_.agents.collect(Agent.human).size).toArray ++ simulations.map(_.agents.collect(Agent.human).size).grouped(10).map(_.last)
  }


  def zombiesDynamic(results: SimulationResult) = {
    val (simulations, _) = results
    simulations.take(1).map(_.agents.collect(Agent.zombie).size).toArray ++  simulations.map(_.agents.collect(Agent.zombie).size).grouped(10).map(_.last)
  }


  def rescuedDynamic(results: SimulationResult) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.rescued).size).grouped(10).map(_.sum)
  }

  def killedDynamic(results: SimulationResult) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.killed).size).grouped(10).map(_.sum)
  }


  def zombifiedDynamic(results: SimulationResult) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.zombified).size).grouped(10).map(_.sum)
  }

  def fleeDynamic(results: SimulationResult) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.flee).size).grouped(10).map(_.sum)
  }

  def pursueDynamic(results: SimulationResult) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.pursue).size).grouped(10).map(_.sum)
  }

  def humansGoneDynamic(results: SimulationResult) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.humanGone).size).grouped(10).map(_.sum)
  }

  def zombiesGoneDynamic(results: SimulationResult) = {
    val (_, events) = results
    Array(0) ++ events.map(_.collect(Event.zombieGone).size).grouped(10).map(_.sum)
  }


}
